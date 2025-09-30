-- ==========================================
-- CCINFOM S24 GROUP 10
-- IT Services Billing & Client Management System
-- DBAPPSCRIPT.sql
-- ==========================================

CREATE DATABASE IF NOT EXISTS ITServicesDB;
USE ITServicesDB;


-- Client Table
CREATE TABLE Client (
    clientId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(200)
);

-- Service Table
CREATE TABLE Service (
    serviceId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    rate DECIMAL(10,2),
    category VARCHAR(50)
);

-- Branch Table
CREATE TABLE Branch (
    branchId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    region VARCHAR(50),
    contactNumber VARCHAR(20)
);

-- Account Manager Table
CREATE TABLE AccountManager (
    managerId VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(50),
    contactInfo VARCHAR(100),
    branchId VARCHAR(20),
    FOREIGN KEY (branchId) REFERENCES Branch(branchId)
);

-- Contract Table
CREATE TABLE Contract (
    contractId VARCHAR(20) PRIMARY KEY,
    clientId VARCHAR(20),
    managerId VARCHAR(20),
    branchId VARCHAR(20),
    startDate DATE,
    endDate DATE,
    terms TEXT,
    FOREIGN KEY (clientId) REFERENCES Client(clientId),
    FOREIGN KEY (managerId) REFERENCES AccountManager(managerId),
    FOREIGN KEY (branchId) REFERENCES Branch(branchId)
);

-- ContractService Table
CREATE TABLE ContractService (
    contractId VARCHAR(20),
    serviceId VARCHAR(20),
    PRIMARY KEY (contractId, serviceId),
    FOREIGN KEY (contractId) REFERENCES Contract(contractId),
    FOREIGN KEY (serviceId) REFERENCES Service(serviceId)
);

-- Invoice Table
CREATE TABLE Invoice (
    invoiceId VARCHAR(20) PRIMARY KEY,
    contractId VARCHAR(20),
    clientId VARCHAR(20),
    invoiceDate DATE,
    dueDate DATE,
    amount DECIMAL(10,2),
    status ENUM('Unpaid','Paid'),
    FOREIGN KEY (contractId) REFERENCES Contract(contractId),
    FOREIGN KEY (clientId) REFERENCES Client(clientId)
);

-- Payment Table
CREATE TABLE Payment (
    paymentId VARCHAR(20) PRIMARY KEY,
    invoiceId VARCHAR(20),
    clientId VARCHAR(20),
    paymentDate DATE,
    amount DECIMAL(10,2),
    method VARCHAR(50),
    referenceNumber VARCHAR(50),
    FOREIGN KEY (invoiceId) REFERENCES Invoice(invoiceId),
    FOREIGN KEY (clientId) REFERENCES Client(clientId)
);

-- 3. Insert Sample Data

-- Branches
INSERT INTO Branch VALUES
('BR-001','Main Office','123 Main St','Metro Manila','09171234567'),
('BR-002','North Branch','456 North Ave','Metro Manila','09179876543');

-- Account Managers
INSERT INTO AccountManager VALUES
('AM-001','John Calvara','Manager','john.calvara@example.com','BR-001'),
('AM-002','Kyle Escario','Manager','kyle.escario@example.com','BR-002');

-- Clients
INSERT INTO Client VALUES
('CL-001','Aubrey Jan','aubrey_jan@gmail.com','09170001111','101 Street A'),
('CL-002','Maxi Tatum','maxiT@yahoo.com','09170002222','202 Street B');

-- Services
INSERT INTO Service VALUES
('SV-001','Web Development','Building responsive websites',50000,'Development'),
('SV-002','Network Setup','Configure office network',20000,'IT Support'),
('SV-003','Software Maintenance','Ongoing software support',15000,'Maintenance');

-- Contracts
INSERT INTO Contract VALUES
('CT-001','CL-001','AM-001','BR-001','2025-09-01','2026-08-31','Annual web development project'),
('CT-002','CL-002','AM-002','BR-002','2025-10-01','2026-09-30','Quarterly IT support');

-- Contract Services
INSERT INTO ContractService VALUES
('CT-001','SV-001'),
('CT-001','SV-003'),
('CT-002','SV-002');

-- Invoices
INSERT INTO Invoice VALUES
('INV-001','CT-001','CL-001','2025-09-15','2025-09-30',65000,'Unpaid'),
('INV-002','CT-002','CL-002','2025-10-15','2025-10-30',20000,'Unpaid');

-- Payments
INSERT INTO Payment VALUES
('PM-001','INV-001','CL-001','2025-09-20',65000,'Bank Transfer','REF-001');

