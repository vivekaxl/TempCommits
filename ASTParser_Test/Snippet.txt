
// Reads xml file and returns different tags in separate methods. 
// For more tags, use an array.
package scan;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/*
 * @author Marcel Patek
 * @version 1
 * @since July 2013
 * 
 */
public class JAXBReader {
    String Reader1() {
        try {
            //Settings settings = JAXB.unmarshall(new File("file.xml"), Settings.class);
            File file = new File("nboscan_settings.xml");

		JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Customer customer = (Customer) jaxbUnmarshaller.unmarshal(new File("C:\\file.xml"));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
        }
        }