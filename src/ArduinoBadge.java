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
    
	public static void main(String args[]) throws ClassNotFoundException, SQLException, FileProblemException {

    	ArduinoBadge db = new ArduinoBadge();
        db.Connection();
		
		// Salvo in listPort la lista delle porte seriale presenti
    	String[] listPort = ArduinoBadge.listPort();
        
    	// Permetto la scelta della porta seriale da utilizzare
    	Scanner s = new Scanner(System.in);
		int chosenPort = s.nextInt();
		s.close();
        SerialPort serialPort = new SerialPort(listPort[chosenPort-1]);

        String input = "";
        
        try {
        	// Apro la porta seriale
            System.out.println("Port opened: " + serialPort.openPort());
            // Imposto i parametri della porta seriale 
            System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0));
            
            while(true) {
            	if (serialPort.readString()!=null) {
            		input = serialPort.readString(7); 
            		System.out.println("Letto: " + input);
                	if (db.DBFind(input)) {
                		System.out.println("Invio ad Arduino: " + serialPort.writeByte((byte)1));
                	}
                	System.out.println("\n");
            	}
            }
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
	}
	
	public void Connection() throws ClassNotFoundException, SQLException, FileProblemException {
		
        System.out.println("\n0) inizializzazione.............................");
		// carichiamo il bridge per SQLite
		Class.forName("org.sqlite.JDBC");
		// NOTA: per sqlite non serve password in realtà!
		conn = DriverManager.getConnection("jdbc:sqlite:files/DB_Badge.db", "root", "root");
		
		System.out.println("\n1) creazione tabelle............................");
		// creiamo la struttura delle tabella
		stmt = conn.createStatement();
		String sql = FileUtilities.read(new File("files/CreateDB_Badge.sql"));
		stmt.executeUpdate(sql);
		stmt.close();
		
	}
	
	public boolean DBFind(String input) throws ClassNotFoundException, SQLException, FileProblemException {
		
		//String input = "1234";
		// Instantiate a Date object
	    Date date = new Date();
		
		System.out.println("\n2) Controllo Badge............................");
		stmt = conn.createStatement();
		String queryFindBadge = "SELECT * FROM Employees WHERE CodBadge = '" + input + "'";
		int id_Employee = 0;
		String nameEmployee = "";
		String sexEmployee = "";

		ResultSet rs = stmt.executeQuery(queryFindBadge);
		if (rs.next()) {
			//System.out.println("Employee con codice " + input + " is present in DataBase");
			nameEmployee = rs.getString("Name");
			sexEmployee = rs.getString("Sex");
			id_Employee = rs.getInt("id_Employee");	
			String queryEmployeeAtWork = "SELECT * FROM EmployeesAtWork WHERE id_Employee = '" + id_Employee + "'";
			 
			rs = stmt.executeQuery(queryEmployeeAtWork);
			if (rs.next()) {
				String queryDeleteEmployee = "DELETE FROM EmployeesAtWork WHERE id_Employee = '" + id_Employee + "'";
				stmt.executeUpdate(queryDeleteEmployee);
				String queryHistory = "INSERT INTO History (Name, Status, Date) VALUES('" + nameEmployee + "','USCITO','" + date.toString() +"')";
				stmt.executeUpdate(queryHistory);
				System.out.println("Arrivederci! " + nameEmployee);
				rs.close();
				stmt.close();
				return true;
			} else {
				if (sexEmployee.equals("M")) System.out.println("Benvenuto! " + nameEmployee);
				else if (sexEmployee.equals("F")) System.out.println("Benvenuta! " + nameEmployee);
				String queryInsert = "INSERT INTO EmployeesAtWork (id_Employee, Date) VALUES('" + id_Employee + "','" + date.toString() +"')";
				String queryHistory = "INSERT INTO History (Name, Status, Date) VALUES('" + nameEmployee + "','ENTRATO','" + date.toString() +"')";
				stmt.executeUpdate(queryInsert);
				stmt.executeUpdate(queryHistory);
				rs.close();
				stmt.close();
				return true;
			}
		} else {
			System.out.println("Employee con codice " + input + " is not present in DataBase");
			System.out.println("Invio ad Arduino: ERROR");
			rs.close();
			stmt.close();
			return false;
		}
	}
	
	
	public static String[] listPort() {	
		// Salvo in portNames la lista delle porte seriali
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