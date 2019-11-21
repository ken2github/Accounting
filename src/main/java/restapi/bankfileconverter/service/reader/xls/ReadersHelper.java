package restapi.bankfileconverter.service.reader.xls;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class ReadersHelper {

	private static final String TITLE_ITEM_PATTERN = "[%s : %s]";
	private static final String TITLE_PATTERN = "%s - OTHER DATA - %s";

	public static class TitleItem {
		private String name;
		private String value;

		public TitleItem(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	public static String getAutoTitle(String description, List<TitleItem> titleItems) {
		return String.format(TITLE_PATTERN, description,
				titleItems.stream().map(
						titleItem -> String.format(TITLE_ITEM_PATTERN, titleItem.name.toUpperCase(), titleItem.value))
						.collect(Collectors.joining(", ")));
	}

	public static Date excelDateParse(int excelDate, int minusDays) {
		Date result = null;
		try {
			GregorianCalendar gc = new GregorianCalendar(1900, Calendar.JANUARY, 1, 0, 0, 0);
			gc.add(Calendar.DATE, excelDate - minusDays);
			result = gc.getTime();
		} catch (RuntimeException e1) {
			e1.printStackTrace();
			throw new RuntimeException("NOT_IMPLEMENTED");
		}
		return result;
	}

	public static Date excelDateParseWithMonthDayInversion(int excelDate) {
		Date date = excelDateParse(excelDate, 0);

		GregorianCalendar gc = new GregorianCalendar(1900, Calendar.JANUARY, 1, 0, 0, 0);
		gc.set(Calendar.YEAR, date.getYear() + 1900);
		gc.set(Calendar.MONTH, date.getDate() - 3);
		gc.set(Calendar.DAY_OF_MONTH, date.getMonth() + 1);

		// System.out.println("");
		// SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		// System.out.println(excelDate);
		// System.out.println(sf.format(date));
		// System.out.println("MONTH " + date.getMonth());
		// System.out.println("DAY " + date.getDate());
		// System.out.println(sf.format(gc.getTime()));
		// System.out.println("");

		return gc.getTime();
	}

	public static BigDecimal valueOfBigDecimalRoundedAtCentimes(double amount) {
		return new BigDecimal(Math.round(amount * 100)).movePointLeft(2);
	}

	public static Date valueOfDateFromString(String date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("NOT_IMPLEMENTED");
		}
	}

}
