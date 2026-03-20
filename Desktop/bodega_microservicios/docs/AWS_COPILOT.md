# Despliegue en AWS (ECS Fargate + RDS) con AWS Copilot

Esta guía asume la app Copilot **`bodega`** (ver `copilot/.workspace`) y un entorno **`dev`**. Ajusta nombres si usas otros.

> **Aviso:** AWS Copilot tiene [fecha de fin de soporte anunciada](https://aws.amazon.com/blogs/containers/announcing-the-end-of-support-for-the-aws-copilot-cli/). Sigue siendo útil para levantar el stack rápido; para producción a largo plazo valora CDK, Terraform u otras opciones.

## Requisitos

- [AWS CLI](https://docs.aws.amazon.com/cli/) configurado (`aws configure`).
- [AWS Copilot CLI](https://aws.github.io/copilot-cli/docs/getting-started/install/).
- Docker (para `copilot svc deploy` con build local).

## Qué incluye el repo

| Ruta | Uso |
|------|-----|
| `copilot/.workspace` | Nombre de la aplicación Copilot (`bodega`). |
| `copilot/environments/dev/manifest.yml` | Manifiesto de entorno de ejemplo. |
| `copilot/environments/addons/bodega-postgres.yml` | RDS PostgreSQL 16 compartido + SG (solo tráfico desde el SG del entorno). |
| `copilot/environments/addons/addons.parameters.yml` | Enlaza VPC/subnets/SG del stack de entorno Copilot. |
| `copilot/*/manifest.yml` | Un servicio ECS por microservicio; `api-gateway` es **Load Balanced Web Service** (ALB público). |

**Service Connect** (`network.connect: true`) permite usar el mismo patrón de host que en Docker: `http://discovery-service:8761/eureka/`.

**Limitación del addon RDS:** está pensado para una **VPC creada/gestionada por Copilot** con recursos `VPC`, `PrivateSubnet1`, `PrivateSubnet2` y `EnvironmentSecurityGroup`. Si importas VPC propia, tendrás que adaptar `addons.parameters.yml` o sustituir el addon por RDS creado a mano.

## 1. Inicializar la aplicación (si aún no existe)

Desde la raíz del monorepo:

```bash
cd /ruta/a/bodega_microservicios
copilot app init bodega
```

Si ya tienes `copilot/.workspace` con `application: bodega`, puedes omitir este paso o alinear el nombre con el que elijas.

## 2. Crear y desplegar el entorno

```bash
copilot env init --name dev --default-config
copilot env deploy --name dev
```

Si tu versión de Copilot espera los addons bajo el directorio del entorno y no los detecta en `copilot/environments/addons/`, copia `addons/` dentro de `copilot/environments/dev/addons/` y vuelve a ejecutar `copilot env deploy`.

Esto despliega la VPC/cluster del entorno y el **addon** `bodega-postgres` (RDS + secreto `{{app}}-{{env}}-bodega-pg-master` en Secrets Manager, etiquetado para Copilot).

## 3. Crear las cuatro bases en la misma instancia RDS

La instancia solo crea el motor; debes crear `auth_db`, `catalog_db`, `inventory_db` y `dispatch_db` (mismo usuario `bodegaadmin`, contraseña en el secreto).

Opciones típicas:

- **Query Editor v2** de RDS (si lo tienes habilitado), o
- Un bastión / tarea temporal con `psql` en la misma VPC.

Ejemplo SQL:

```sql
CREATE DATABASE auth_db;
CREATE DATABASE catalog_db;
CREATE DATABASE inventory_db;
CREATE DATABASE dispatch_db;
```

(Extensiones Flyway/JPA de cada servicio poblarán el esquema al arrancar.)

## 4. Desplegar microservicios (orden recomendado)

Copilot no modela `depends_on` entre servicios; el orden evita errores de registro en Eureka o llamadas REST antes de tiempo.

```bash
copilot svc deploy --name discovery-service --env dev
copilot svc deploy --name auth-service       --env dev
copilot svc deploy --name catalog-service    --env dev
copilot svc deploy --name inventory-service  --env dev
copilot svc deploy --name dispatch-service   --env dev
copilot svc deploy --name api-gateway      --env dev
```

Cada manifiesto construye la imagen con contexto **raíz del repo** y el `Dockerfile` del módulo correspondiente.

## 5. Probar el API público

Tras el despliegue del gateway:

```bash
copilot svc show --name api-gateway --env dev
```

Usa el **URL del balanceador** (o dominio si configuraste alias) y llama a rutas expuestas por el gateway (por ejemplo `/auth/...`, `/products/...`).

## Variables y secretos relevantes

| Variable / secreto | Origen |
|--------------------|--------|
| `EUREKA_DEFAULT_ZONE` | Fija en manifests: `http://discovery-service:8761/eureka/` (Service Connect). |
| `SPRING_DATASOURCE_URL` | `from_cfn` → export `{{app}}-{{env}}-BodegaJdbc*Db` del addon. |
| `SPRING_DATASOURCE_USERNAME` | `bodegaadmin` (coincide con el template del secreto). |
| `SPRING_DATASOURCE_PASSWORD` | Secrets Manager: `${COPILOT_APPLICATION_NAME}-${COPILOT_ENVIRONMENT_NAME}-bodega-pg-master`, clave JSON `password`. |

Si cambias el nombre de la app o del entorno, los exports de CloudFormation y el nombre del secreto deben seguir coincidiendo con lo que esperan los manifiestos.

## Costes y ajustes rápidos

- `db.t4g.micro` + 20 GB es un punto de partida barato; sube `DBInstanceClass` / almacenamiento en `bodega-postgres.yml` para cargas reales.
- Aumenta `cpu`/`memory` en los manifiestos si los arranques Spring quedan cortos de memoria.

## Solución de problemas

1. **Addon falla con VPC importada:** revisa `addons.parameters.yml` y alinea `!Ref` con los recursos reales de tu stack de entorno.
2. **`secretsmanager` denegado:** el secreto debe llevar las etiquetas `copilot-application` y `copilot-environment` (el addon ya las define).
3. **Fallo JPA “database does not exist”:** crea las cuatro bases (paso 3).
4. **Gateway 503 / sin rutas:** confirma que todos los servicios aparecen en Eureka y que el orden de despliegue fue el del paso 4.
