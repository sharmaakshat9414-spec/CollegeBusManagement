-- =====================================================================
--  College Bus Tracking & Route Management System
--  Poornima Institute of Engineering and Technology (PIET), Jaipur
--  Database : college_bus_db      (MySQL 5.7 / 8.x)
--
--  Run this whole file once. Running it again RESETS the database
--  (tables are dropped and recreated with the sample data).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS college_bus_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE college_bus_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS student_bus_allocations;
DROP TABLE IF EXISTS bus_assignments;
DROP TABLE IF EXISTS notices;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS drivers;
DROP TABLE IF EXISTS buses;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- users : login accounts for ADMIN, STUDENT and DRIVER
-- (password_hash = SHA-256 of the password, as produced by the Java app)
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('ADMIN','STUDENT','DRIVER') NOT NULL,
    status        ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL UNIQUE,
    full_name  VARCHAR(100) NOT NULL,
    reg_no     VARCHAR(30)  NOT NULL UNIQUE,
    email      VARCHAR(120) NOT NULL UNIQUE,
    phone      VARCHAR(15)  NOT NULL,
    section    VARCHAR(30)  NOT NULL,
    status     ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE drivers (
    driver_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL UNIQUE,
    name       VARCHAR(100) NOT NULL,
    phone      VARCHAR(15)  NOT NULL,
    license_no VARCHAR(40)  NOT NULL UNIQUE,
    address    VARCHAR(255) NOT NULL,
    email      VARCHAR(120) NOT NULL UNIQUE,
    status     ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_drivers_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE buses (
    bus_id          INT AUTO_INCREMENT PRIMARY KEY,
    bus_number      VARCHAR(20) NOT NULL UNIQUE,
    registration_no VARCHAR(30) NOT NULL UNIQUE,
    bus_type        VARCHAR(30) NOT NULL,
    capacity        INT NOT NULL CHECK (capacity > 0),
    status          ENUM('ACTIVE','INACTIVE','MAINTENANCE') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE routes (
    route_id       INT AUTO_INCREMENT PRIMARY KEY,
    route_name     VARCHAR(100) NOT NULL UNIQUE,
    start_point    VARCHAR(100) NOT NULL,
    destination    VARCHAR(100) NOT NULL,
    stops          TEXT NOT NULL,
    distance_km    DECIMAL(6,1) NOT NULL,
    est_time_min   INT NOT NULL,
    departure_time VARCHAR(5) NOT NULL DEFAULT '07:30',
    status         ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

-- Bus + Driver + Route (a bus's driver and route are read from its ACTIVE assignment)
CREATE TABLE bus_assignments (
    assignment_id   INT AUTO_INCREMENT PRIMARY KEY,
    bus_id          INT NOT NULL,
    driver_id       INT NOT NULL,
    route_id        INT NOT NULL,
    assignment_date DATE NOT NULL,
    status          ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_assign_bus    FOREIGN KEY (bus_id)    REFERENCES buses(bus_id)     ON DELETE RESTRICT,
    CONSTRAINT fk_assign_driver FOREIGN KEY (driver_id) REFERENCES drivers(driver_id) ON DELETE RESTRICT,
    CONSTRAINT fk_assign_route  FOREIGN KEY (route_id)  REFERENCES routes(route_id)  ON DELETE RESTRICT
) ENGINE=InnoDB;

-- One allocation per student
CREATE TABLE student_bus_allocations (
    allocation_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL UNIQUE,
    bus_id        INT NOT NULL,
    route_id      INT NOT NULL,
    pickup_stop   VARCHAR(100) NOT NULL,
    status        ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_alloc_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_alloc_bus     FOREIGN KEY (bus_id)     REFERENCES buses(bus_id)        ON DELETE RESTRICT,
    CONSTRAINT fk_alloc_route   FOREIGN KEY (route_id)   REFERENCES routes(route_id)     ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE notices (
    notice_id   INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    notice_date DATE NOT NULL,
    status      ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB;

-- =====================================================================
--  SAMPLE DATA
--  Admin    : admin / admin123
--  Drivers  : (email) / driver123
--  Students : (registration number or email) / student123
-- =====================================================================

INSERT INTO users (user_id, username, email, password_hash, full_name, role, status) VALUES
(1,  'admin', 'admin@piet.edu', SHA2('admin123', 256), 'System Administrator', 'ADMIN', 'ACTIVE'),
(2,  'rajesh.sharma@piet.edu',   'rajesh.sharma@piet.edu',   SHA2('driver123', 256), 'Rajesh Kumar Sharma',   'DRIVER', 'ACTIVE'),
(3,  'mohan.gurjar@piet.edu',    'mohan.gurjar@piet.edu',    SHA2('driver123', 256), 'Mohan Lal Gurjar',      'DRIVER', 'ACTIVE'),
(4,  'sunil.yadav@piet.edu',     'sunil.yadav@piet.edu',     SHA2('driver123', 256), 'Sunil Kumar Yadav',     'DRIVER', 'ACTIVE'),
(5,  'bhanwar.shekhawat@piet.edu','bhanwar.shekhawat@piet.edu', SHA2('driver123', 256), 'Bhanwar Singh Shekhawat', 'DRIVER', 'ACTIVE'),
(6,  'PIET23CS001', 'aarav.sharma@example.com',   SHA2('student123', 256), 'Aarav Sharma',     'STUDENT', 'ACTIVE'),
(7,  'PIET23CS014', 'priya.verma@example.com',    SHA2('student123', 256), 'Priya Verma',      'STUDENT', 'ACTIVE'),
(8,  'PIET23EC022', 'rohan.gupta@example.com',    SHA2('student123', 256), 'Rohan Gupta',      'STUDENT', 'ACTIVE'),
(9,  'PIET23IT007', 'sneha.joshi@example.com',    SHA2('student123', 256), 'Sneha Joshi',      'STUDENT', 'ACTIVE'),
(10, 'PIET22ME031', 'karan.rathore@example.com',  SHA2('student123', 256), 'Karan Singh Rathore', 'STUDENT', 'ACTIVE'),
(11, 'PIET22CS045', 'ananya.mehta@example.com',   SHA2('student123', 256), 'Ananya Mehta',     'STUDENT', 'ACTIVE'),
(12, 'PIET24CS058', 'vikram.choudhary@example.com', SHA2('student123', 256), 'Vikram Choudhary', 'STUDENT', 'ACTIVE'),
(13, 'PIET24EC019', 'neha.agarwal@example.com',   SHA2('student123', 256), 'Neha Agarwal',     'STUDENT', 'ACTIVE'),
(14, 'PIET23IT033', 'rahul.meena@example.com',    SHA2('student123', 256), 'Rahul Meena',      'STUDENT', 'ACTIVE'),
(15, 'PIET24ME012', 'pooja.saini@example.com',    SHA2('student123', 256), 'Pooja Saini',      'STUDENT', 'ACTIVE');

INSERT INTO drivers (driver_id, user_id, name, phone, license_no, address, email, status) VALUES
(1, 2, 'Rajesh Kumar Sharma',      '9876500011', 'RJ14-20150012345', 'Mansarovar, Jaipur',     'rajesh.sharma@piet.edu',   'ACTIVE'),
(2, 3, 'Mohan Lal Gurjar',         '9876500012', 'RJ14-20120067890', 'Vaishali Nagar, Jaipur', 'mohan.gurjar@piet.edu',    'ACTIVE'),
(3, 4, 'Sunil Kumar Yadav',        '9876500013', 'RJ14-20180024680', 'Sanganer, Jaipur',       'sunil.yadav@piet.edu',     'ACTIVE'),
(4, 5, 'Bhanwar Singh Shekhawat',  '9876500014', 'RJ14-20100013579', 'Jhotwara, Jaipur',       'bhanwar.shekhawat@piet.edu','ACTIVE');

INSERT INTO students (student_id, user_id, full_name, reg_no, email, phone, section, status, created_at) VALUES
(1,  6,  'Aarav Sharma',        'PIET23CS001', 'aarav.sharma@example.com',   '9811100001', 'CSE-A', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2,  7,  'Priya Verma',         'PIET23CS014', 'priya.verma@example.com',    '9811100002', 'CSE-A', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 27 DAY)),
(3,  8,  'Rohan Gupta',         'PIET23EC022', 'rohan.gupta@example.com',    '9811100003', 'ECE-A', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 24 DAY)),
(4,  9,  'Sneha Joshi',         'PIET23IT007', 'sneha.joshi@example.com',    '9811100004', 'IT-A',  'ACTIVE', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(5,  10, 'Karan Singh Rathore', 'PIET22ME031', 'karan.rathore@example.com',  '9811100005', 'ME-A',  'ACTIVE', DATE_SUB(NOW(), INTERVAL 16 DAY)),
(6,  11, 'Ananya Mehta',        'PIET22CS045', 'ananya.mehta@example.com',   '9811100006', 'CSE-B', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(7,  12, 'Vikram Choudhary',    'PIET24CS058', 'vikram.choudhary@example.com','9811100007','CSE-B', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(8,  13, 'Neha Agarwal',        'PIET24EC019', 'neha.agarwal@example.com',   '9811100008', 'ECE-B', 'ACTIVE', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(9,  14, 'Rahul Meena',         'PIET23IT033', 'rahul.meena@example.com',    '9811100009', 'IT-A',  'ACTIVE', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 15, 'Pooja Saini',         'PIET24ME012', 'pooja.saini@example.com',    '9811100010', 'ME-B',  'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO routes (route_id, route_name, start_point, destination, stops, distance_km, est_time_min, departure_time, status) VALUES
(1, 'Route 1 - Mansarovar',    'Mansarovar Metro Station', 'PIET Campus, Sitapura',
    'Mansarovar Metro Station, Shipra Path, Pratap Nagar, Sanganer, Sitapura', 24.5, 55, '07:00', 'ACTIVE'),
(2, 'Route 2 - Vaishali Nagar','Vaishali Nagar Chauraha',  'PIET Campus, Sitapura',
    'Vaishali Nagar Chauraha, Gopalpura Bypass, Durgapura, Tonk Phatak, Sitapura', 22.0, 50, '07:10', 'ACTIVE'),
(3, 'Route 3 - Malviya Nagar', 'Malviya Nagar',            'PIET Campus, Sitapura',
    'Malviya Nagar, Jagatpura Phatak, Sanganer, Sitapura', 16.0, 40, '07:30', 'ACTIVE'),
(4, 'Route 4 - Jhotwara',      'Jhotwara Circle',          'PIET Campus, Sitapura',
    'Jhotwara Circle, Civil Lines, Sindhi Camp, Railway Station, Sitapura', 28.0, 65, '06:50', 'ACTIVE'),
(5, 'Route 5 - Sodala',        'Sodala',                   'PIET Campus, Sitapura',
    'Sodala, Bani Park, Railway Station, Sitapura', 26.0, 60, '07:00', 'INACTIVE');

INSERT INTO buses (bus_id, bus_number, registration_no, bus_type, capacity, status) VALUES
(1, 'PIET-01', 'RJ14-PA-1101', 'AC Bus',      50, 'ACTIVE'),
(2, 'PIET-02', 'RJ14-PA-1102', 'Non-AC Bus',  52, 'ACTIVE'),
(3, 'PIET-03', 'RJ14-PA-1103', 'AC Bus',      45, 'ACTIVE'),
(4, 'PIET-04', 'RJ14-PA-1104', 'Mini Bus',    30, 'ACTIVE'),
(5, 'PIET-05', 'RJ14-PA-1105', 'Non-AC Bus',  52, 'MAINTENANCE'),
(6, 'PIET-06', 'RJ14-PA-1106', 'Electric Bus',40, 'INACTIVE');

INSERT INTO bus_assignments (bus_id, driver_id, route_id, assignment_date, status) VALUES
(5, 1, 1, DATE_SUB(CURDATE(), INTERVAL 120 DAY), 'INACTIVE'),
(1, 1, 1, DATE_SUB(CURDATE(), INTERVAL 60 DAY),  'ACTIVE'),
(2, 2, 2, DATE_SUB(CURDATE(), INTERVAL 55 DAY),  'ACTIVE'),
(3, 3, 3, DATE_SUB(CURDATE(), INTERVAL 40 DAY),  'ACTIVE'),
(4, 4, 4, DATE_SUB(CURDATE(), INTERVAL 20 DAY),  'ACTIVE');

INSERT INTO student_bus_allocations (student_id, bus_id, route_id, pickup_stop, status) VALUES
(1, 1, 1, 'Mansarovar Metro Station', 'ACTIVE'),
(2, 1, 1, 'Shipra Path',              'ACTIVE'),
(3, 2, 2, 'Vaishali Nagar Chauraha',  'ACTIVE'),
(4, 2, 2, 'Gopalpura Bypass',         'ACTIVE'),
(5, 3, 3, 'Malviya Nagar',            'ACTIVE'),
(6, 3, 3, 'Jagatpura Phatak',         'ACTIVE'),
(7, 4, 4, 'Jhotwara Circle',          'ACTIVE'),
(8, 4, 4, 'Civil Lines',              'ACTIVE');

INSERT INTO notices (title, description, notice_date, status) VALUES
('Bus timing changed for Route 1',
 'From Monday, Route 1 (Mansarovar) will depart at 07:00 AM instead of 07:15 AM. Please reach your pickup stop 5 minutes early.',
 DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ACTIVE'),
('Maintenance notice - PIET-05',
 'Bus PIET-05 is under scheduled maintenance this week. Affected students will be informed about alternate arrangements.',
 DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'ACTIVE'),
('Route 4 stop update',
 'Route 4 (Jhotwara) now includes a pickup at Civil Lines. Students may request a pickup change from the transport office.',
 DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'ACTIVE'),
('Holiday notice',
 'College transport will not operate on the upcoming public holiday. Regular services resume the next working day.',
 DATE_SUB(CURDATE(), INTERVAL 9 DAY), 'ACTIVE'),
('Important transport announcement',
 'Students must carry their college ID card while travelling in college buses. Drivers will not allow entry without ID.',
 DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'ACTIVE');
