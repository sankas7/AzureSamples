package blobQuickstart.blobAzureApp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.text.StringSubstitutor;


public class PropertyReaderUtility {

	
	public static  Properties readPropertiesFile(String fileName) throws IOException {
		InputStream fis = null;
		Properties prop = null;
		try {
			prop = new Properties();
			fis = PropertyReaderUtility.class.getResourceAsStream(fileName);
 
			// create Properties class object
			if (fis != null) {
				// load properties file into it
				prop.load(fis);
			} else {
				throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
			}
 
		} catch (FileNotFoundException e) {
 
			e.printStackTrace();
		} catch (IOException e) {
 
			e.printStackTrace();
		} finally {
			fis.close();
		}
 
		return prop;
	}
	
	
	public static Map<String,String> loadPropertiesMap(String fileName) throws IOException {
		InputStream fis  = PropertyReaderUtility.class.getResourceAsStream(fileName);
	    final Map<String, String> ordered = new LinkedHashMap<String, String>();
	    //Hack to use properties class to parse but our map for preserved order
	    Properties bp = new Properties() {
	        @Override
	        public synchronized Object put(Object key, Object value) {
	            ordered.put((String)key, (String)value);
	            return super.put(key, value);
	        }
	    };
	    bp.load(fis);
	    final Map<String,String> resolved = new LinkedHashMap<String, String>(ordered.size());
	    
	    StringSubstitutor sub = new StringSubstitutor( key -> 
	     {
	    	 String value = resolved.get(key);
	            if (value == null)
	                return System.getProperty(key);
	            return value;
	    });
	    
	    
	    for (String k : ordered.keySet()) {
	        String value = sub.replace(ordered.get(k));
	        resolved.put(k, value);
	    }
	    return resolved;
	}
}
