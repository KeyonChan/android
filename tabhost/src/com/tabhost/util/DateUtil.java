package com.tabhost.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	/**
	 * 返回统计时间字符串，如01:00。在用的地方开启一个线程,while(isRunning)
	 */
	public static String getJishi(int timeSec){
		int min = timeSec/60;
		int sec = timeSec%60;
		String result="";
		if(min<10)
			result = "0"+min;
		else
			result = min+"";
		result += ":";
		if(sec<10)
			result += "0"+sec;
		else
			result += sec;
		return result;
	}
	public static long datetimeToTimestamp(String time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = df.parse(getStandardTimeStr(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static String timestampToDatetime(long timestamp){
		Date date = new Date(timestamp);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // yyyy年MM月dd日 HH时mm分ss秒
		
		return df.format(date);
	}

	//转为标准格式字符串
	public static String getStandardTimeStr(String timeStr){
		String date, time, year, mon, day, hour, min = null , sec = null;
		if(timeStr.contains(" ")){
			date = timeStr.split(" ")[0];
			time = timeStr.split(" ")[1];
			switch(time.split(":").length){
			case 1:
				hour = time.split(":")[0];
				min = "00";
				sec = "00";
				break;
			case 2:
				hour = time.split(":")[0];
				min = time.split(":")[1];
				sec = "00";
				break;
			case 3:
				hour = time.split(":")[0];
				min = time.split(":")[1];
				sec = time.split(":")[2];
				break;
			}
			hour = time.split(":")[0];
		}else{
			date = timeStr;
			hour = "00";
			min = "00";
			sec = "00";
		}
		year = date.split("-")[0];
		mon = date.split("-")[1];
		day = date.split("-")[2];

		if(year.length()<3){
			year = "20"+year;
		}
		if(mon.length()<2){
			mon = "0"+mon;
		}
		if(day.length()<2){
			day = "0"+day;
		}
		if(hour.length()<2){
			hour = "0"+hour;
		}
		if(min.length()<2){
			min = "0"+min;
		}
		if(sec.length()<2){
			sec = "0"+sec;
		}
		//System.out.println(year+"-"+mon+"-"+day+" "+hour+":"+min+":"+sec);
		return year+"-"+mon+"-"+day+" "+hour+":"+min+":"+sec;
	}

	public static String getNow(){
		Date date = new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // yyyy年MM月dd日 HH时mm分ss秒
		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return df.format(date);
	}

	public static String getDateFromStr(String str){
		return getStandardTimeStr(str).substring(0, 10);
	}

	public static String getShortDateFromStr(String str){
		return getStandardTimeStr(str).substring(2, 10);
	}

	public static String getDatetimeFromStr(String str){
		return getStandardTimeStr(str).substring(0, 19);
	}

	public static String getShortDatetimeFromStr(String str){
		return getStandardTimeStr(str).substring(2, 19);
	}

	//type:0-秒  1-分  2-时 3-天；count为负表示减
	public static String addDatetime(String datetime, int type,int count){
		//Date date = new Date(System.currentTimeMillis()+ 3 * 24 * 60 * 60 * 1000);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		Date d1 = null;
		try {
			d1 = df.parse(getStandardTimeStr(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long timestamp = d1.getTime();
		Date date = null;
		switch (type) {
		case 0:
			date = new Date(timestamp+ count * 1000);
			break;
		case 1:
			date = new Date(timestamp+ count * 1000 * 60);
			break;
		case 2:
			date = new Date(timestamp+ count * 1000 * 60 * 60);
			break;
		case 3:
			date = new Date(timestamp+ count * 1000 * 60 * 60 * 24);
			break;
		default:
			break;
		}
		return df.format(date);
	}

	//从今天开始的时间加减
	public static String addDatetimeFromNow(int type,int count){
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		Date date = null;
		switch (type) {
		case 0:
			date = new Date(timestamp+ count * 1000);
			break;
		case 1:
			date = new Date(timestamp+ count * 1000 * 60);
			break;
		case 2:
			date = new Date(timestamp+ count * 1000 * 60 * 60);
			break;
		case 3:
			date = new Date(timestamp+ count * 1000 * 60 * 60 * 24);
			break;
		default:
			break;
		}
		return df.format(date);
	}

	public static long secondDiffDiffDate(String small, String big){
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d1 = df.parse(getStandardTimeStr(big));
			Date d2 = df.parse(getStandardTimeStr(small));
			long timestamp = d1.getTime() - d2.getTime();
			long secondDiff = timestamp / 1000;
			return secondDiff;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static long secondDiffToNow(String time){
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = df.parse(getStandardTimeStr(time));
			long timestamp = System.currentTimeMillis() - date.getTime();
			long secondDiff = timestamp / 1000;
			return secondDiff;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static long secondDiffFromNow(String time){
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = df.parse(getStandardTimeStr(time));
			long timestamp = date.getTime() - System.currentTimeMillis();
			long secondDiff = timestamp / 1000;
			return secondDiff;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	//倒计时：时分秒
	public static String[] getDaojishi(long timestamp){
		long now = System.currentTimeMillis();
		long secDiff = timestamp - now;
		if(secDiff<=0){
			return new String[]{"00", "00", "00"};
		}
		int hour = (int) (secDiff/(60*60*1000));
		int min = (int) (secDiff%(60*60*1000)/1000/60); //取余仍然是毫秒
		int sec = (int) (secDiff%(60*60*1000)/1000%60);
		String[] time = new String[]{hour+"", min+"", sec+""};
		if(hour<10){
			time[0]="0"+hour;
		}
		if(min<10){
			time[1]="0"+min;
		}
		if(sec<10){
			time[2]="0"+sec;
		}
		System.out.println(time[0] +" "+time[1] +" "+time[2]);
		return time;
	}
}
