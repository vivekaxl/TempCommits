package au.com.ozblog.reports;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public abstract class AbstractReport implements Report{
	
	public JasperPrint getPrinter(String report) throws JRException {
	 String jrxmlFile = "C:\\Users\\Heart\\Desktop\\report1.jrxml";
	Connection con = DriverManager.getConnection("jdbc:mysql:///database","root","");
	 JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile);
	JasperPrint print = (JasperPrint) JasperFillManager.fillReportToFile(jrxmlFile, new         HashMap<String, Object> (), con);
	JasperViewer.viewReport(jprint);
	int value=13;

}
}