-- Create database
CREATE DATABASE IF NOT EXISTS employee_management;
USE employee_management;

-- Create tables for core entities
CREATE TABLE positions (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name IN ('ADMIN', 'MANAGER', 'EMPLOYEE'))
);

CREATE TABLE user_roles (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Modified employee table with:
-- 1. Only name field
-- 2. position_name instead of position_code
-- 3. Added gender field
-- 4. Added start_date field
-- 5. Qualification directly in the employee table
CREATE TABLE employees (
    emp_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')),
    start_date DATE NOT NULL,
    position_name VARCHAR(100) NOT NULL,
    qualification VARCHAR(50) CHECK (qualification IN ('Diploma', 'Bachelor', 'Master', 'PHD', 'Professor'))
);

-- Insert basic data
INSERT INTO roles (name) VALUES ('ADMIN'), ('MANAGER'), ('EMPLOYEE');

-- Add indexes for performance
CREATE INDEX idx_employee_position ON employees(position_name);
CREATE INDEX idx_employee_start_date ON employees(start_date);
