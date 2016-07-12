package tasklogger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
}


