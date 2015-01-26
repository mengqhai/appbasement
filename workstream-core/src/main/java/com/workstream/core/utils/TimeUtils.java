package com.workstream.core.utils;

import java.util.Date;
import java.util.TimeZone;

/**
 * See http://www.thetimenow.com/gmt/greenwich_mean_time
 * 
 * @author liuli
 * 
 */
public class TimeUtils {

	public static long convertTimezone(TimeZone fromTimeZone,
			TimeZone toTimeZone, Date fromTime) {
		long gmtLong = fromTime.getTime() + fromTimeZone.getRawOffset();
		long toTimeInMillis = gmtLong - toTimeZone.getRawOffset();
		return toTimeInMillis;
	}

	public static long convertTimezone(TimeZone toTimeZone, Date fromTime) {
		return convertTimezone(toTimeZone, TimeZone.getDefault(), fromTime);
	}

	public static long convertTimezone(String timeZoneId, Date fromTime) {
		return convertTimezone(TimeZone.getTimeZone(timeZoneId), fromTime);
	}

}
