import java.sql.Connection;

public class ConnectionProvider {
	public static Connection getConnection() throws SQLException{
		Connection conn = null;
		if(cn == null){
		    String driver = "com.mysql.jdbc.Driver";
		    Class.forName(driver);
		    dbHost = "jdbc:mysql://"+dbHost;
		    cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);
		}
	}
}