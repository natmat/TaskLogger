package tasklogger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date; 

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TLUtilities {

	public static String getHMSString(long totalTime) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(new Date(totalTime));
		return time;
	}

	public static boolean isValidName(String taskName, String invalidString) {		
		boolean isValid = false;
		if ((taskName != null) 
				&& !taskName.isEmpty() 
				&& !(taskName.equals(invalidString))) { 
			isValid = true;
		}
		return(isValid);
	}

	public static String getToday() { 
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		return(dateFormat.format(cal.getTime()));
	}
	
	public static String getOsDirectory() {
//		String osName = System.getProperty("os.name").toLowerCase();
//		String cwd = System.getProperty("user.dir");
		return null;
	}
	
	public enum ePropertyNames {
		TASK_STATE_CHANGE
	}
	
	public static void importFromExcel() {
		try {
			Workbook workbook = WorkbookFactory.create(new File("resources/typhoon.xlsm"));
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


