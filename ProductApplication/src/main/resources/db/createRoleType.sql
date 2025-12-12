DROP TYPE IF EXISTS "${database.defaultSchemaName}".role_type CASCADE;
CREATE TYPE "${database.defaultSchemaName}".role_type AS ENUM ('ROLE_USER','ROLE_ADMIN');