package junit.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

public class Demo {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {

		String d = "1980-12-31";
		
		
		/*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = df.parse(d);
		System.out.println(date);*/
		
		DateLocaleConverter dlc = new DateLocaleConverter();
		dlc.convert(d, "yyyy-MM-dd");
		
	}

}
