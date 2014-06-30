import java.util.ArrayList;
import java.util.List;


class DataCollectorSchema {
	int precision;
	String name;
	int lineNumber;
	String type;
	List<String> elements;
	int character;
	
	public DataCollectorSchema() {
		precision=0;
		name="";
		lineNumber=0;
		type="";
		elements = new ArrayList<String>();
		character=0;
	}

}
