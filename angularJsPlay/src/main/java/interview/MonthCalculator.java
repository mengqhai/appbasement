package interview;

import java.util.Calendar;
import java.util.Date;

public class MonthCalculator {

	public MonthCalculator() {
	}

	/**
	 * By natural month
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean differOneMonth(Date start, Date end) {
		int tDiff = 1;
		Calendar cStart = Calendar.getInstance();
		cStart.setTime(start);
		Calendar cEnd = Calendar.getInstance();
		cEnd.setTime(end);
		int diffMonth = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR))
				* 12 + (cEnd.get(Calendar.MONTH) - cStart.get(Calendar.MONTH));

		if (diffMonth > tDiff) {
			return false;
		}

		

		/**
		 * 2014-01-27 2014-02-27 = 1 month 
		 * 2014-01-31 2014-02-27 < 1 month
		 * 2014-01-31 2014-02-28 = 1 month 
		 * 2014-01-30 2014-02-28 > 1 month
		 * 
		 */

		return false;
	}

//	private boolean isEndOfMonth(Calendar cStart, Calendar cEnd) {
//		/**
//		 * The number of days in the end month, e.g. for 2014-01, it's 31, for
//		 * 2014-02, it's 28.
//		 */
////		int dayCountInEnd = cEnd.getActualMaximum(Calendar.MONTH);
////		int dayOfMonth = cEnd.get
//		
//	}

}
