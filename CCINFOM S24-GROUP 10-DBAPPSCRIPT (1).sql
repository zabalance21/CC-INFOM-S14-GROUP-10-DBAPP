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
    category VARCHAR(50),
    availability ENUM('Available', 'Discontinued') DEFAULT 'Available'
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
    position VARCHAR(50),
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
    branchId VARCHAR(20),
    startDate DATE,
    endDate DATE,
    terms TEXT,
    contract_status ENUM('Active', 'Expired', 'Closed') DEFAULT 'Active',
    FOREIGN KEY (clientId) REFERENCES Client(clientId),
    FOREIGN KEY (managerId) REFERENCES AccountManager(managerId),
    FOREIGN KEY (branchId) REFERENCES Branch(branchId)
);

-- ===========================
-- CONTRACT SERVICE TABLE
-- ===========================
CREATE TABLE ContractService (
    contractId VARCHAR(20),
    serviceId VARCHAR(20),
    PRIMARY KEY (contractId, serviceId),
    FOREIGN KEY (contractId) REFERENCES Contract(contractId),
    FOREIGN KEY (serviceId) REFERENCES Service(serviceId)
);

-- ===========================
-- INVOICE TABLE
-- ===========================
CREATE TABLE Invoice (
    invoiceId VARCHAR(20) PRIMARY KEY,
    contractId VARCHAR(20),
    clientId VARCHAR(20),
    invoiceDate DATE,
    dueDate DATE,
    amount DECIMAL(10,2),
    lateFee DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('Unpaid','Paid','Overdue','Cancelled') DEFAULT 'Unpaid',
    FOREIGN KEY (contractId) REFERENCES Contract(contractId),
    FOREIGN KEY (clientId) REFERENCES Client(clientId)
);

-- ===========================
-- PAYMENT TABLE
-- ===========================
CREATE TABLE Payment (
    paymentId VARCHAR(20) PRIMARY KEY,
    invoiceId VARCHAR(20),
    clientId VARCHAR(20),
    paymentDate DATE,
    amount DECIMAL(10,2),
    method VARCHAR(50),
    referenceNumber VARCHAR(50),
    payment_status ENUM('Valid','Refunded','Pending') DEFAULT 'Valid',
    FOREIGN KEY (invoiceId) REFERENCES Invoice(invoiceId),
    FOREIGN KEY (clientId) REFERENCES Client(clientId)
);

-- ===========================
-- INSERT SAMPLE DATA
-- ===========================

-- Branches
INSERT INTO Branch (branchId, name, address, city, contactNumber, status) VALUES
('BR-001','Main Office','123 Main St','Manila','09171234567','Operational'),
('BR-002','North Branch','456 North Ave','Makaki','09179876543','Operational');

-- Account Managers
INSERT INTO AccountManager (managerId, name, position, contactInfo, branchId, employment_status) VALUES
('AM-001','John Calvara','Manager','john.calvara@example.com','BR-001','Active'),
('AM-002','Kyle Escario','Manager','kyle.escario@example.com','BR-002','Active');

-- Clients
INSERT INTO Client (clientId, name, email, phone, address, status) VALUES
('CL-001','Aubrey Jan','aubrey_jan@gmail.com','09170001111','101 Street A','Active'),
('CL-002','Maxi Tatum','maxiT@yahoo.com','09170002222','202 Street B','Active');

-- Services
INSERT INTO Service (serviceId, name, description, rate, category, availability) VALUES
('SV-001','Web Development','Building responsive websites',50000,'Development','Available'),
('SV-002','Network Setup','Configure office network',20000,'IT Support','Available'),
('SV-003','Software Maintenance','Ongoing software support',15000,'Maintenance','Available');

-- Contracts
INSERT INTO Contract (contractId, clientId, managerId, branchId, startDate, endDate, terms, contract_status) VALUES
('CT-001','CL-001','AM-001','BR-001','2025-09-01','2026-08-31','Annual web development project','Active'),
('CT-002','CL-002','AM-002','BR-002','2025-10-01','2026-09-30','Quarterly IT support','Active');

-- Contract Services
INSERT INTO ContractService (contractId, serviceId) VALUES
('CT-001','SV-001'),
('CT-001','SV-003'),
('CT-002','SV-002');

-- Invoices
INSERT INTO Invoice (invoiceId, contractId, clientId, invoiceDate, dueDate, amount, status) VALUES
('INV-001','CT-001','CL-001','2025-09-15','2025-09-30',65000,'Unpaid'),
('INV-002','CT-002','CL-002','2025-10-15','2025-10-30',20000,'Unpaid');

-- Payments
INSERT INTO Payment (paymentId, invoiceId, clientId, paymentDate, amount, method, referenceNumber, payment_status) VALUES
('PM-001','INV-001','CL-001','2025-09-20',65000,'Bank Transfer','REF-001','Valid');
