package collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
public class ListFrequency {
 
    public static void main(String a[]){
    	int count;
        List<String> ll = new ArrayList<String>();
        ll.add("one");
        ll.add("two");
        ll.add("three");
        ll.add("four");
        ll.add("two");
        ll.add("three");
        ll.add("two");
        ll.add("one");
        System.out.println("Actual list: "+ll);

	count = Collections.frequency(list, "B"); 

    }

}