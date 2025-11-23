-- Создание схемы custom
CREATE SCHEMA IF NOT EXISTS "${database.defaultSchemaName}" AUTHORIZATION CURRENT_USER;

SET search_path TO "${database.defaultSchemaName}";