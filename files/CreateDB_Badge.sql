DROP TABLE IF EXISTS Employees;
DROP TABLE IF EXISTS EmployeesAtWork;
DROP TABLE IF EXISTS History;

CREATE TABLE Employees (
	id_Employee INTEGER PRIMARY KEY AUTOINCREMENT,
	Name TEXT,
	Sex TEXT,
	CodBadge TEXT
);

CREATE TABLE History (
	Name TEXT,
	Status TEXT,
	Date TEXT
);

CREATE TABLE EmployeesAtWork (
	id_Employee INTEGER,
	Date TEXT,
	CONSTRAINT EmployeesAtWork_Employees FOREIGN KEY (id_Employee) REFERENCES Employees(id_Employee)
);

INSERT INTO Employees (Name,Sex,CodBadge) VALUES('Emanuele','M','74F8483');
INSERT INTO Employees (Name,Sex,CodBadge) VALUES('Alessia','F','8C166AC');