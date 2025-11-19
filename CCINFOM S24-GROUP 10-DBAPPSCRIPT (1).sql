-- ==========================================
-- CCINFOM S24 GROUP 10
-- IT Services Billing & Client Management System
-- DBAPPSCRIPT.sql
-- ==========================================

DROP DATABASE IF EXISTS ITServices;
CREATE DATABASE ITServices;
USE ITServices;


-- ===========================
-- CLIENT TABLE
-- ===========================
CREATE TABLE Client (
    clientId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(200),
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

-- ===========================
-- SERVICE TABLE
-- ===========================
CREATE TABLE Service (
    serviceId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    rate DECIMAL(10,2),
    availability ENUM('Available', 'Unavailable', 'Discontinued') DEFAULT 'Available'
);

-- ===========================
-- BRANCH TABLE
-- ===========================
CREATE TABLE Branch (
    branchId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    city VARCHAR(50),
    contactNumber VARCHAR(20),
    status ENUM('Operational', 'Closed') DEFAULT 'Operational'
);

-- ===========================
-- ACCOUNT MANAGER TABLE
-- ===========================
CREATE TABLE AccountManager (
    managerId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contactInfo VARCHAR(100),
    branchId VARCHAR(20),
    employment_status ENUM('Active', 'Resigned') DEFAULT 'Active',
    FOREIGN KEY (branchId) REFERENCES Branch(branchId)
);

-- ===========================
-- CONTRACT TABLE
-- ===========================
CREATE TABLE Contract (
    contractId VARCHAR(20) PRIMARY KEY,
    clientId VARCHAR(20),
    managerId VARCHAR(20),
    startDate DATE,
    endDate DATE,
    contract_status ENUM('Active', 'Closed') DEFAULT 'Active',
    FOREIGN KEY (clientId) REFERENCES Client(clientId),
    FOREIGN KEY (managerId) REFERENCES AccountManager(managerId)
);

-- ===========================
-- CONTRACT SERVICE TABLE
-- ===========================
CREATE TABLE ContractService (
	csId VARCHAR(20) PRIMARY KEY,
    contractId VARCHAR(20),
    serviceId VARCHAR(20),
    status ENUM('Active', 'Inactive') DEFAULT 'Active',
    FOREIGN KEY (contractId) REFERENCES Contract(contractId),
    FOREIGN KEY (serviceId) REFERENCES Service(serviceId)
);

-- ===========================
-- INVOICE TABLE
-- ===========================
CREATE TABLE Invoice (
    invoiceId VARCHAR(20) PRIMARY KEY,
    contractId VARCHAR(20),
    invoiceDate DATE,
    dueDate DATE,
    amount DECIMAL(10,2),
    lateFee DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('Unpaid','Paid','Overdue','Cancelled') DEFAULT 'Unpaid',
    FOREIGN KEY (contractId) REFERENCES Contract(contractId)
);

-- ===========================
-- PAYMENT TABLE
-- ===========================
CREATE TABLE Payment (
    paymentId VARCHAR(20) PRIMARY KEY,
    invoiceId VARCHAR(20),
    paymentDate DATE,
    amount DECIMAL(10,2),
    referenceNumber VARCHAR(50),
    payment_status ENUM('Valid','Refunded','Pending') DEFAULT 'Valid',
    FOREIGN KEY (invoiceId) REFERENCES Invoice(invoiceId)
);


INSERT INTO Branch (branchId, name, address, city, contactNumber, status) VALUES
('BR-001','Manila Branch','123 Main St','Manila','09171234567','Operational'),
('BR-002','Makati Branch','456 North Ave','Makati','09179876543','Operational'),
('BR-003','Quezon City Branch','789 Central Road','Quezon City','09175551234','Operational'),
('BR-004','Cebu Branch','101 Cebu St','Cebu','09175552345','Operational'),
('BR-005','Davao Branch','202 Davao Blvd','Davao','09175553456','Operational'),
('BR-006','Taguig Branch','303 BGC Lane','Taguig','09175554567','Operational'),
('BR-007','Pasig Branch','404 Ortigas Ave','Pasig','09175555678','Closed'),
('BR-008','Baguio Branch','505 Pine Road','Baguio','09175556789','Operational'),
('BR-009','Iloilo Branch','606 Iloilo St','Iloilo','09175557890','Operational'),
('BR-010','Batangas Branch','707 Batangas Road','Batangas','09175558901','Operational');

INSERT INTO AccountManager (managerId, name, contactInfo, branchId, employment_status) VALUES
('AM-001','John Calvara','john.calvara@example.com','BR-001','Active'),
('AM-002','Kyle Escario','kyle.escario@example.com','BR-002','Active'),
('AM-003','Arlene Sy','arlene.sy@example.com','BR-003','Active'),
('AM-004','Rico Santos','rico.santos@example.com','BR-004','Active'),
('AM-005','Mara Gomez','mara.gomez@example.com','BR-005','Active'),
('AM-006','Daniel Cruz','dan.cruz@example.com','BR-006','Active'),
('AM-007','Fiona Lim','fiona.lim@example.com','BR-007','Resigned'),
('AM-008','George Yu','george.yu@example.com','BR-008','Active'),
('AM-009','Paula Reyes','paula.reyes@example.com','BR-009','Active'),
('AM-010','Chris Tan','chris.tan@example.com','BR-010','Active');

INSERT INTO Service (serviceId, name, description, rate, availability) VALUES
('SV-001','Web Development','Building responsive websites',50000.00,'Available'),
('SV-002','Network Setup','Configure office network',20000.00,'Available'),
('SV-003','Software Maintenance','Ongoing software support',15000.00,'Available'),
('SV-004','Cloud Migration','Move systems to cloud infrastructure',80000.00,'Available'),
('SV-005','Cybersecurity Audit','Evaluate system vulnerabilities',60000.00,'Available'),
('SV-006','Technical Support','24/7 IT support services',12000.00,'Available'),
('SV-007','Database Optimization','Improve DB performance',40000.00,'Available'),
('SV-008','Mobile App Development','Create mobile apps',70000.00,'Available'),
('SV-009','Hardware Installation','Install computers & servers',25000.00,'Available'),
('SV-010','Network Security Monitoring','Continuous security monitoring',30000.00,'Available');

INSERT INTO Client (clientId, name, email, phone, address, status) VALUES
('CL-001','Aubrey Jan','aubrey_jan@gmail.com','09170001111','101 Street A','Active'),
('CL-002','Maxi Tatum','maxiT@yahoo.com','09170002222','202 Street B','Active'),
('CL-003','Kyra Michaels','kyra_mich@example.com','09170003333','303 Cedar Road','Active'),
('CL-004','Liam Cruz','liam_cruz@example.com','09170004444','404 Maple Street','Active'),
('CL-005','Samantha Lee','samlee@example.com','09170005555','505 Palm Village','Active'),
('CL-006','Carlos Reyes','carlos.reyes@example.com','09170006666','606 Oak Avenue','Active'),
('CL-007','Julia Tan','julia.tan@example.com','09170007777','707 Sunset Blvd','Active'),
('CL-008','Nathan Torres','nathan.t@example.com','09170008888','808 Emerald Lane','Active'),
('CL-009','Bea Santos','bea.santos@example.com','09170009999','909 Silver St','Active'),
('CL-010','Derek Miller','dmiller@example.com','09171234568','010 Gold Ave','Inactive');

INSERT INTO Contract (contractId, clientId, managerId, startDate, endDate, contract_status) VALUES
('CT-001','CL-001','AM-001','2026-01-10','2027-01-10','Active'),
('CT-002','CL-002','AM-002','2026-02-01','2027-02-01','Active'),
('CT-003','CL-003','AM-003','2026-03-05','2027-03-05','Active'),
('CT-004','CL-005','AM-004','2025-11-01','2026-11-01','Closed'),
('CT-005','CL-006','AM-005','2026-05-12','2027-05-12','Active'),
('CT-006','CL-007','AM-006','2026-06-01','2027-06-01','Active'),
('CT-007','CL-008','AM-008','2026-07-20','2027-07-20','Active'),
('CT-008','CL-009','AM-009','2026-08-15','2027-08-15','Active'),
('CT-009','CL-002','AM-009','2026-09-10','2027-09-10','Active'),
('CT-010','CL-001','AM-001','2026-10-01','2027-10-01','Active');

INSERT INTO ContractService (csId, contractId, serviceId, status) VALUES
('CS-001','CT-001','SV-001','Active'),
('CS-002','CT-001','SV-003','Active'),
('CS-003','CT-002','SV-002','Active'),
('CS-004','CT-003','SV-004','Active'),
('CS-005','CT-003','SV-006','Active'),
('CS-006','CT-004','SV-005','Inactive'),
('CS-007','CT-005','SV-007','Active'),
('CS-008','CT-005','SV-009','Active'),
('CS-009','CT-006','SV-008','Active'),
('CS-010','CT-007','SV-010','Active'),
('CS-011','CT-008','SV-001','Active'),
('CS-012','CT-008','SV-002','Active'),
('CS-013','CT-009','SV-003','Active'),
('CS-014','CT-009','SV-007','Active'),
('CS-015','CT-010','SV-004','Active'),
('CS-016','CT-010','SV-005','Active');

INSERT INTO Invoice (invoiceId, contractId, invoiceDate, dueDate, amount, lateFee, status) VALUES
('INV-001','CT-001','2026-01-25','2026-02-24',65000.00,0.00,'Unpaid'),
('INV-002','CT-002','2026-02-10','2026-03-11',20000.00,0.00,'Paid'),
('INV-003','CT-003','2026-03-20','2026-04-19',92000.00,0.00,'Unpaid'),
('INV-005','CT-005','2026-05-28','2026-06-27',65000.00,0.00,'Paid'),
('INV-006','CT-006','2026-06-15','2026-07-15',70000.00,0.00,'Unpaid'),
('INV-007','CT-007','2026-07-30','2026-08-29',30000.00,0.00,'Unpaid'),
('INV-008','CT-008','2026-08-01','2026-08-31',70000.00,0.00,'Unpaid'),
('INV-009','CT-009','2026-09-20','2026-10-20',55000.00,0.00,'Unpaid'),
('INV-010','CT-010','2026-10-20','2026-11-19',140000.00,0.00,'Unpaid');

INSERT INTO Payment (paymentId, invoiceId, paymentDate, amount, referenceNumber, payment_status) VALUES
('PM-001','INV-002','2026-02-15',20000.00,'REF-002','Valid'),
('PM-002','INV-005','2026-06-10',65000.00,'REF-005','Valid'),
('PM-003','INV-003','2026-04-01',46000.00,'REF-003A','Valid'),
('PM-004','INV-003','2026-04-10',46000.00,'REF-003B','Valid'),
('PM-005','INV-007','2026-08-05',30000.00,'REF-007','Valid'),
('PM-006','INV-001','2026-02-10',65000.00,'REF-001','Valid'),
('PM-007','INV-009','2026-10-05',55000.00,'REF-009','Valid'),
('PM-008','INV-010','2026-11-01',70000.00,'REF-010A','Valid'),
('PM-009','INV-010','2026-11-10',70000.00,'REF-010B','Valid'),
('PM-010','INV-006','2026-07-20',70000.00,'REF-006','Valid');

