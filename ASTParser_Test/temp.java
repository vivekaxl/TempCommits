class SmartCopyTestClass{
void SmartCopytestClass(){
if(cn == null){
String driver = "com.mysql.jdbc.Driver"; 
Class.forName(driver); 
dbHost = "jdbc:mysql://"+dbHost;
cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);
System.out.println("test");
}
}
}
