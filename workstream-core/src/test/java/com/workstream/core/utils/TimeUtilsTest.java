package com.workstream.core.utils;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilsTest {

	@Test
	public void testConvertTimeZone() {
		TimeZone fromTimeZone = TimeZone.getTimeZone("Etc/GMT+8");
		Date fromTime = new Date();
		TimeZone toTimeZone = TimeZone.getTimeZone("ETC/GMT+0");
		long toTimeLong = TimeUtils.convertTimezone(fromTimeZone, toTimeZone,
				fromTime);
		Assert.assertEquals(8, (fromTime.getTime() - toTimeLong)
				/ (60 * 60 * 1000));
		toTimeZone = TimeZone.getTimeZone("ETC/GMT+9");
		toTimeLong = TimeUtils.convertTimezone(fromTimeZone, toTimeZone,
				fromTime);
		Assert.assertEquals(-1, (fromTime.getTime() - toTimeLong)
				/ (60 * 60 * 1000));
	}

}
