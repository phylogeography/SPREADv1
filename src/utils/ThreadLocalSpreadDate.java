package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ThreadLocalSpreadDate {

	private static ThreadLocal<Calendar> calThreadLocal;
	// private Calendar cal;
	private SimpleDateFormat formatter;
	private Date stringdate;

	public ThreadLocalSpreadDate(String date) throws ParseException {

		// if no era specified assume current era
		String line[] = date.split(" ");
		if (line.length == 1) {
			StringBuilder properDateStringBuilder = new StringBuilder();
			date = properDateStringBuilder.append(date).append(" AD")
					.toString();
		}

		formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);
		stringdate = formatter.parse(date);

		// Make calls to Calendar class in thread safe way
		calThreadLocal = new ThreadLocal<Calendar>() {

			@Override
			protected Calendar initialValue() {
				return Calendar.getInstance();
			}
		};

	}// END: ThreadLocalSpreadDate()

	public long plus(int days) {
		Calendar cal = calThreadLocal.get();
		cal.setTime(stringdate);
		cal.add(Calendar.DATE, days);
		return cal.getTimeInMillis();
	}// END: plus

	public long minus(int days) {
		Calendar cal = calThreadLocal.get();
		cal.setTime(stringdate);
		cal.add(Calendar.DATE, -days);
		return cal.getTimeInMillis();
	}// END: minus

	public long getTime() {
		Calendar cal = calThreadLocal.get();
		cal.setTime(stringdate);
		return cal.getTimeInMillis();
	}// END: getDate

}// END: class
