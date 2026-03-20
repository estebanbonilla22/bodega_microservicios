-- Replace placeholder plaintext password with BCrypt hash for local/dev.
-- The initial V1__init.sql uses 'CHANGE_ME' as a placeholder.
UPDATE users
SET password_hash = crypt('CHANGE_ME', gen_salt('bf'))
WHERE password_hash = 'CHANGE_ME';

