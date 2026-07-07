-- =====================================================================
-- ATS - Sistema de Reclutamiento
-- Script de esquema y datos de prueba para las 3 bases de datos.
-- Cada microservicio crea sus tablas automáticamente (ddl-auto: update),
-- este script sirve como respaldo/documentación y para poblar datos
-- de prueba en un entorno limpio.
-- =====================================================================

-- =====================================================================
-- db_usuarios (ms-usuarios)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS db_usuarios;
USE db_usuarios;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

-- password en texto plano: candidato123 / admin123 (hash BCrypt)
INSERT INTO usuarios (nombre, email, password, rol) VALUES
    ('Juan Perez', 'juan.perez@example.com', '$2a$10$B.dN3kmUSMsJ4lH5lk59.uPmJ70N7fvhg0n7bAnQY1fHJDq/M5ZiS', 'CANDIDATO'),
    ('Maria Admin', 'maria.admin@example.com', '$2a$10$RjvFNwdnl4Chi1QS2x5G7.7bUyt2qXGSkhfdTOAV7Tt8cW94g9JyC', 'ADMIN');

-- =====================================================================
-- db_vacantes (ms-vacantes)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS db_vacantes;
USE db_vacantes;

CREATE TABLE IF NOT EXISTS vacantes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    requisitos VARCHAR(2000) NOT NULL,
    salario DOUBLE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_publicacion DATETIME NOT NULL
);

INSERT INTO vacantes (titulo, descripcion, requisitos, salario, estado, fecha_publicacion) VALUES
    ('Desarrollador Backend Java', 'Desarrollo de microservicios con Spring Boot', 'Java 21, Spring Boot, MySQL, 2+ años de experiencia', 5500.00, 'ABIERTA', NOW()),
    ('Analista QA', 'Pruebas funcionales y automatizadas', 'Experiencia con Selenium y Postman', 3800.00, 'ABIERTA', NOW()),
    ('DevOps Engineer', 'Gestión de infraestructura y despliegues', 'Docker, Kubernetes, CI/CD', 6200.00, 'CERRADA', NOW());

-- =====================================================================
-- db_postulaciones (ms-postulaciones)
-- =====================================================================
CREATE DATABASE IF NOT EXISTS db_postulaciones;
USE db_postulaciones;

CREATE TABLE IF NOT EXISTS postulaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_vacante BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_postulacion DATETIME NOT NULL
);

-- Referencia: id_usuario 1 = Juan Perez (CANDIDATO), id_vacante 1 y 2 = vacantes abiertas creadas arriba
INSERT INTO postulaciones (id_vacante, id_usuario, estado, fecha_postulacion) VALUES
    (1, 1, 'PENDIENTE', NOW()),
    (2, 1, 'EN_REVISION', NOW());
