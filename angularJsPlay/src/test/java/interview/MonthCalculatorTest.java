package interview;

import static junitparams.JUnitParamsRunner.$;

import java.text.SimpleDateFormat;
import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = JUnitParamsRunner.class)
public class MonthCalculatorTest {

	private MonthCalculator calculator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		calculator = new MonthCalculator();
	}

	@After
	public void tearDown() throws Exception {
	}

	public Object[] getDateArgs() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		return $($(fmt.parse("2014-01-01"), fmt.parse("2014-02-01"), true),
				$(fmt.parse("2014-01-31"), fmt.parse("2014-02-28"), true),
				$(fmt.parse("2014-01-15"), fmt.parse("2014-04-15"), false),
				$(fmt.parse("2012-01-01"), fmt.parse("2014-02-01"), false));
	}

	@Test
	@Parameters(method = "getDateArgs")
	public void testDifferOneMonth(Date date1, Date date2, boolean isTrue)
			throws Exception {
		Assert.assertEquals(isTrue, calculator.differOneMonth(date1, date2));
	}

	@Test
	public void testByJoda() {
		DateTime start = DateTime.parse("2013-12-30");
		DateTime end = DateTime.parse("2014-02-28");
		Months m = Months.monthsBetween(start, end);
		System.out.println("Months between " + start.toString("yyyy-MM-dd")
				+ " and " + end.toString("yyyy-MM-dd") + ": " + m.getMonths());
	}

}
