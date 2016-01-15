import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ProvaJDBC {
  public static void main (String args[]) throws ClassNotFoundException, SQLException, FileProblemException{
   
	  System.out.println("\n0) inizializzazione.............................");
		// carichiamo il bridge per SQLite
		Class.forName("org.sqlite.JDBC");
		// NOTA: per sqlite non serve password in realtà!
		Connection conn = DriverManager.getConnection("jdbc:sqlite:files/DB_Badge.db", "root", "root");
		
		System.out.println("\n1) creazione tabelle............................");
		// creiamo la struttura delle tabella
		Statement stmt = conn.createStatement();
		String sql = FileUtilities.read(new File("files/CreateDB_Badge.sql"));
		stmt.executeUpdate(sql);
		stmt.close();
		
		String input = "1234";
		
		System.out.println("\n2) Controllo Badge............................");
		stmt = conn.createStatement();
		String queryFindBadge = "SELECT * FROM Employees WHERE CodBadge = '" + input +"'";
		int id_Employee = -10;
		
		ResultSet rs = stmt.executeQuery(queryFindBadge);
		if (rs.next()) { 
			System.out.println("Employee con codice " + input + " is present in DataBase");
			id_Employee = rs.getInt("id_Employee");
			String queryInsert = "INSERT INTO EmployeesAtWork (id_Employee) VALUES('" + id_Employee + "')";
			if(stmt.executeUpdate(queryInsert)==1) {
				System.out.println("Dato inserito");
				System.out.println("Invio ad Arduino: OK");
			}
		}
		else {
			System.out.println("Employee con codice " + input + " is not present in DataBase");
			System.out.println("Invio ad Arduino: ERROR");
		}
		rs.close();
		stmt.close();
  }
}