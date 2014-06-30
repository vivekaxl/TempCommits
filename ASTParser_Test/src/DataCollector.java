import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DataCollector {
	List<DataCollectorSchema> listClasses;
	public DataCollector() {
		listClasses = new ArrayList<DataCollectorSchema>();
	}
	public void insertData(String input){
		outerparser(input);
	}
	public List<DataCollectorSchema> getData(){
		return listClasses;
	}
	public void printData(){
		for(DataCollectorSchema temp:listClasses){
			System.out.println("Precision: " + temp.precision);
			System.out.println("Name: " + temp.name);
			System.out.println("Line Number: "+temp.lineNumber);
			System.out.println("Type: " + temp.type);
			System.out.println("Elements: ");
//			for(String str:temp.elements){
//				System.out.println(str);
//			}
			System.out.println("Character :" + temp.character);
			System.out.println();
		}
	}
	
	List<DataCollectorSchema> outerparser(String input){
		List<String> tempOuters = new ArrayList<String>();
		Pattern p = Pattern.compile("\\{([^}]*)\\}");
		Matcher m = p.matcher(input);
		while (m.find()) {
		 tempOuters.add(m.group(1));
		}
		for(String tempOuter:tempOuters){
			listClasses.add(innerParser(tempOuter));
		}
		return listClasses;
	}
	
	DataCollectorSchema innerParser(String temp){
		temp.replace("\n", "").replace("\r", "");
		DataCollectorSchema returnValue = new DataCollectorSchema();
		Pattern p = Pattern.compile("\\\"precision\": \"([^}]*)\\\"name\"");
		Matcher m = p.matcher(temp);
		while (m.find()) {
			returnValue.precision = Integer.parseInt(m.group(1).replace("\"", "").replace(",","").replace("\n","").replace(" ", ""));
			}
		
		p = Pattern.compile("\\\"name\": \"([^}]*)\\\"line_number\"");
		m = p.matcher(temp);
		while (m.find()) {
			 returnValue.name = m.group(1).replace(" ","").replace("\n", "");
			}
		
		p = Pattern.compile("\\\"line_number\": \"([^}]*)\\\"type\"");
		m = p.matcher(temp);
		while (m.find()) {
			returnValue.lineNumber = Integer.parseInt(m.group(1).replace("\"", "").replace(",","").replace("\n","").replace(" ",""));
			}
		

		p = Pattern.compile("\\\"type\": \"([^}]*)\\\"elements\"");
		m = p.matcher(temp);
		while (m.find()) {
			 returnValue.type = m.group(1).replace("\"", "").replace(",","").replace("\n","").replace(" ","");
			}
		
		String tempElement = temp.replace("\"\"","");
		p = Pattern.compile("\\\"elements\": \\[([^}]*)\\],");
		m = p.matcher(tempElement);
		while (m.find())
			tempElement = m.group(1);
		tempElement = tempElement.replace(" ","");
		String[] tempElements = tempElement.split(",");
		returnValue.elements = Arrays.asList(tempElements);
		

		p = Pattern.compile("\\\"character\": \"([^}]*)\\\"");
		m = p.matcher(temp);
		while (m.find()) {
			returnValue.character = Integer.parseInt(m.group(1).replace("\"", "").replace(",","").replace(" ",""));
			}
		return returnValue;
	}

}
