package com.workstream.core.utils;

import java.util.Date;
import java.util.TimeZone;

/**
 * See http://www.thetimenow.com/gmt/greenwich_mean_time
 * 
 * As long as we return the long millisecond (since 1970... UTC <-- there is a
 * timezone in it: GMT+0), the browser can automatically convert it to the local
 * time of the browser by new Date(1422286061392).toLocaleString()/.toString(),
 * so this tool seems useless at all.
 * 
 * For example, 1422286061392 represents Mon, 26 Jan 2015 15:27:41
 * GMT(toUTCString()). If the browser is in timezone GMT+9, you'll see the
 * following result: new Date(1422286061392).toString() ==
 * "Tue Jan 27 2015 00:27:41 GMT+0900 (东京标准时间)" <br/>
 * new Date(1422286061392).toLocaleString()=="2015年1月27日 上午12:27:41"
 * 
 * But if the browser is runnning in timezone GMT+8, the same value
 * 1422286061392 will be translated to:
 * "Mon Jan 26 2015 23:27:41 GMT+0800 (中国标准时间)" and "2015年1月26日 下午11:27:41"
 * 
 * For the above reasons, this utility seems useless.
 * 
 * @author liuli
 * 
 */
@Deprecated
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
