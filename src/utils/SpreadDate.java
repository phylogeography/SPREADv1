package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpreadDate {

	private Calendar cal;
	private SimpleDateFormat formatter;
	private Date stringdate;

	public SpreadDate(String date) throws ParseException {

		// if no era specified assume current era
		String line[] = date.split(" ");
		if (line.length == 1) {
			StringBuilder properDateStringBuilder = new StringBuilder();
			date = properDateStringBuilder.append(date).append(" AD")
					.toString();
		}

		formatter = new SimpleDateFormat("yyyy-MM-dd G", Locale.US);
		stringdate = formatter.parse(date);

		cal = Calendar.getInstance();
	}

	public long plus(int days) {
		cal.setTime(stringdate);
		cal.add(Calendar.DATE, days);
		return cal.getTimeInMillis();
	}// END: plus

	public long minus(int days) {
		cal.setTime(stringdate);
		cal.add(Calendar.DATE, -days);
		return cal.getTimeInMillis();
	}// END: minus

	public long getTime() {
		cal.setTime(stringdate);
		return cal.getTimeInMillis();
	}// END: getDate

}
