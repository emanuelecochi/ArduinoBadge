DROP TABLE IF EXISTS Employees;
DROP TABLE IF EXISTS EmployeesAtWork;

CREATE TABLE IF NOT EXISTS Employees (
	id_Employee INTEGER PRIMARY KEY AUTOINCREMENT,
	Name TEXT,
	CodBadge TEXT
);

CREATE TABLE EmployeesAtWork (
	id_EmployeeAtWork INTEGER PRIMARY KEY AUTOINCREMENT,
	id_Employee INTEGER,
	CONSTRAINT EmployeesAtWork_Employees FOREIGN KEY (id_Employee) REFERENCES Employees(id_Employee)
);

INSERT INTO Employees (Name,CodBadge) VALUES('Lele','1234');
INSERT INTO Employees (Name,CodBadge) VALUES('Maria','4321');