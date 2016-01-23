import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class ArduinoBadge {
	
	private Statement stmt = null;
	private Connection conn = null;
	static String[] listPort = null;
	
	public static void main(String args[]) throws ClassNotFoundException, SQLException, FileProblemException {

    	ArduinoBadge db = new ArduinoBadge();
        db.ConnectionDB();
		// Save in listPort the list of the ports present
    	listPort = ArduinoBadge.listPort();
        
    	// Allow the selection of the serial port to use
    	Scanner s = new Scanner(System.in);
		int chosenPort = s.nextInt();
		s.close();
        SerialPort serialPort = new SerialPort(db.listPort[chosenPort-1]);

        String input = "";
        
        try {
        	// Open the serial port
            System.out.println("Port opened: " + serialPort.openPort());
            // Set the serial port parameters
            System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0));
            
            while(true) {
            	if (serialPort.readString()!=null) {
            		// Read the first 7 byte
            		input = serialPort.readString(7); 
            		System.out.println("\nRead: " + input);
                	if (db.CheckBadge(input)) {
                		serialPort.writeByte((byte)1);
                	}
                	System.out.println("\n");
            	}
            }
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
	}
	
	public void ConnectionDB() throws ClassNotFoundException, SQLException, FileProblemException {
		
        System.out.println("\n0) Initialization.............................");
		// Load the bridge for SQLite
		Class.forName("org.sqlite.JDBC");
		// NOTE: for sqlite we don't need the password!
		conn = DriverManager.getConnection("jdbc:sqlite:files/DB_Badge.db", "root", "root");
		
		System.out.println("\n1) Create tables............................\n");
		// Create the structur of the tables
		stmt = conn.createStatement();
		String sql = FileUtilities.read(new File("files/CreateDB_Badge.sql"));
		stmt.executeUpdate(sql);
		stmt.close();
		
	}
	
	public boolean CheckBadge(String input) throws ClassNotFoundException, SQLException, FileProblemException {
		
		// Instantiate a Date object
	    Date date = new Date();
		
		System.out.println("\n2) Check Badge............................");
		stmt = conn.createStatement();
		String queryFindBadge = "SELECT * FROM Employees WHERE CodBadge = '" + input + "'";
		int id_Employee = 0;
		String nameEmployee = "";
		String sexEmployee = "";

		ResultSet rs = stmt.executeQuery(queryFindBadge);
		if (rs.next()) {
			nameEmployee = rs.getString("Name");
			sexEmployee = rs.getString("Sex");
			id_Employee = rs.getInt("id_Employee");	
			String queryEmployeeAtWork = "SELECT * FROM EmployeesAtWork WHERE id_Employee = '" + id_Employee + "'";
			 
			rs = stmt.executeQuery(queryEmployeeAtWork);
			if (rs.next()) {
				String queryDeleteEmployee = "DELETE FROM EmployeesAtWork WHERE id_Employee = '" + id_Employee + "'";
				stmt.executeUpdate(queryDeleteEmployee);
				String queryHistory = "INSERT INTO History (Name, Status, Date) VALUES('" + nameEmployee + "','OUT','" + date.toString() +"')";
				stmt.executeUpdate(queryHistory);
				System.out.println("Goodbye! " + nameEmployee);
				rs.close();
				stmt.close();
				return true;
			} else {
				System.out.println("Welcome! " + nameEmployee);
				String queryInsert = "INSERT INTO EmployeesAtWork (id_Employee, Date) VALUES('" + id_Employee + "','" + date.toString() +"')";
				String queryHistory = "INSERT INTO History (Name, Status, Date) VALUES('" + nameEmployee + "','IN','" + date.toString() +"')";
				stmt.executeUpdate(queryInsert);
				stmt.executeUpdate(queryHistory);
				rs.close();
				stmt.close();
				return true;
			}
		} else {
			System.out.println("ERROR: Employee with code " + input + " is not present in DataBase");
			rs.close();
			stmt.close();
			return false;
		}
	}
	
	
	public static String[] listPort() {	
		// Save in portNames the list of the ports present
		String[] portNames = SerialPortList.getPortNames();
		        
		if (portNames.length == 0) {
		    System.out.println("There are no serial-ports :(");
		    System.out.println("Press Enter to exit...");
		    try {
		        System.in.read();
		    } catch (IOException e) {
		          e.printStackTrace();
		    }
		    return null;
		}

		System.out.println("Choose the port:");
		for (int i = 0; i < portNames.length; i++){
		    System.out.println(i+1 + ": " + portNames[i]);
		}
		
		return portNames;
	}
	
}