package com.iwanol.paypal.until;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class VerifyUtil {
	
	/**正则表达式-非0开头纯数字串*/
	private final static String REGEX_NUMBER = "^[1-9][\\d]*$";
	
	/**正则表达式-手机号码*/
	private final static String REGEX_PHONE_NUMBER = "^[1][\\d]{10}$";
	
	/**正则表达式-身份证号码*/
	private final static String REGEX_ID_NUMBER = "^[1-9][\\d]{16}[\\dxX]$";
	
	/**正则表达式-邮箱*/
	private final static String REGEX_EMAIL = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
	
	
	/**
	 * 非0开头纯数字串校验
	 * @param number
	 * @return
	 */
	public static boolean isNumber(String number) {
		if (StringUtils.isEmpty(number))
			return false;
		
		return Pattern.matches(REGEX_NUMBER, number);
	}

	/**
	 * 手机号码校验
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumber(String phoneNumber) {
		if (StringUtils.isEmpty(phoneNumber))
			return false;
		
		return Pattern.matches(REGEX_PHONE_NUMBER, phoneNumber);
	}
	
	/**
	 * 身份证号码校验
	 * @param idNumber
	 * @return
	 */
	public static boolean isIdNumber(String idNumber) {
		if (StringUtils.isEmpty(idNumber))
			return false;
		
		return Pattern.matches(REGEX_ID_NUMBER, idNumber);
	}
	
	/**
	 * 邮箱校验
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (StringUtils.isEmpty(email))
			return false;
		
		return Pattern.matches(REGEX_EMAIL, email);
	}
}
