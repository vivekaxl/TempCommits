import java.sql.Connection;

public class ConnectionProvider {
	String hostName;
	String userName;
	String userPassword;
	public static Connection getConnection() throws SQLException{
		Connection conn = null;
		if(cn == null){
		    String driver = "com.mysql.jdbc.Driver";
		    Class.forName(driver);
		    dbHost = "jdbc:mysql://"+dbHost;
		    dbUser = 13;
		    cn = DriverManager.getConnection( dbHost, dbUser, dbPassword );
		}
	}
}