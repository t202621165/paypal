package com.iwanol.paypal.until;

import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	/**
	 * 根据指定格式获取当前日期
	 * @return
	 */
	public static String getDate(String pattern){
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	/**
	 * 将日期转换成String 类型
	 * @return
	 */
	public static String getDate(Date date, String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	/**
	 * 将String 类型转换成Date
	 * @return
	 * @throws ParseException 
	 */
	public static Date getDate(String date, String pattern) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.parse(date);
	}
	
	/**
	 * 获取某一日期的零点
	 * @param date
	 * @return
	 */
	public static Date getDateZeroPoint(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
	}
	
	/**
	 * 日期比较
	 * @return
	 */
	public static boolean dateCompare() {
		return false;
	}
	public static void main(String[] args) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		Date date1 = new Date();
		System.out.println("Date1：" + getDate(date1, pattern));
		Date date2 = new Date();
		System.out.println("Date2：" + getDate(date2, pattern));
		int i = date1.compareTo(date2);
		System.out.println(i);
		int j = date2.compareTo(date1);
		System.out.println(j);
		int k = date2.compareTo(getBeforeOrAfterDateByDate(Calendar.MINUTE, -5));
		System.out.println(k);
	}
	/**
	 * 获取当天指定天数内的所有日期
	 * @param pattern
	 * @param counts
	 * @return
	 */
	public static String[] getDayArray(Integer counts) {
		String[] dates = new String[counts];
		int index = counts-1;
		for (int i = 0; i < counts; i++) {
			String date = getBeforeOrAfterDateByDate("yyyy-MM-dd", Calendar.DATE, -index--);
			dates[i] = date;
		}
		return dates;
	}
	
	/**
	 * 获取当前日期之前或之后的日期字符串格式
	 * @param date
	 * 			日期：Date 类型
	 * @param pattern
	 * 			日期格式化模式：yyyy-MM-dd HH:mm:ss
	 * @param field
	 * 			时间域：Calendar.MINUTE(分钟)、.DATE(天)、.MONTH(月)...
	 * @param amount
	 * 			时间域加减数量：-1(减去一天、一个月...)、1(加上一天、一个月...)
	 * @return
	 */
	public static String getBeforeOrAfterDateByDate(String pattern,int field, int amount){
		try {
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(field, amount);
			date = calendar.getTime();
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			return format.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return getDate(pattern);
		}
	}
	
	/**
	 * 获取当前日期之前或之后的日期格式
	 * @param date
	 * 			日期：Date 类型
	 * @param field
	 * 			时间域：Calendar.DATE(天)、Calendar.MONTH(月)...
	 * @param amount
	 * 			时间域加减数量：-1(减去一天、一个月...)、1(加上一天、一个月...)
	 * @return
	 */
	public static Date getBeforeOrAfterDateByDate(int field, int amount){
		Date date = new Date();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(field, amount);
			date = calendar.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将日期字符串转换为Date类型
	 * @param dateFormat
	 * @param pattern
	 * @return
	 */
	public static Date getDateByDateFormat(String dateFormat, String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date = new Date();
		try {
			date = format.parse(dateFormat);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 从百度获取网络时间
	 * @param pattern
	 * @return
	 */
	public static String getNetworkDate(String pattern) {
		String time = getDate(pattern);
		try {
			URL url = new URL("http://www.baidu.com");// 取得资源对象
			URLConnection uc = url.openConnection();// 生成连接对象
			uc.setReadTimeout(5000);// 设置访问超时时间 5s
			uc.connect();// 发出连接
			long ld = uc.getDate();// 读取网站日期时间
			Date date = new Date(ld);// 转换为标准时间对象
			SimpleDateFormat sdf = new SimpleDateFormat(pattern,Locale.CHINA);// 输出北京时间
			time = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}
	
	/**
	 * 获取指定前缀 + 时间戳 + 指定长度后缀的字符串
	 * @param prefix
	 *			前缀
	 * @param suffixLength
	 * 			后缀长度
	 * @return
	 */
	public static String getTimeStampNumber(String prefix, int suffixLength) {
		String suffix = CommonUtil.getRandom(suffixLength);
		String center = getDate("yyMMddHHmmssSSS");
		StringBuffer buf = CommonUtil.getBuffer(prefix,center,suffix);
		return buf.toString();
	}
	
}
