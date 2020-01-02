package com.iwanol.paypal.until;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

/**
 * 公共工具类
 * 
 * @author leo
 *
 */
public class CommonUtil {
	
	/**
	 * 生成随机字符串：字母和数字组合
	 * @param length
	 * @return
	 */
	public static String getStringRandom(int length) {
		StringBuffer buf = new StringBuffer();
		Random random = new Random();
		// 参数length，表示生成几位随机数
		for (int i = 0; i < length; i++) {

			int number = random.nextInt(2);
			// 输出字母还是数字
			if (number % 2 == 0) {
				// 输出是大写字母还是小写字母
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				buf.append((char) (random.nextInt(26) + temp));
			} else {
				buf.append(String.valueOf(random.nextInt(10)));
			}
		}
		return buf.toString();
	}

	/**
	 * 保存token到浏览器cookie
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static void saveTokenToCookie(HttpServletResponse response, String token)
			throws UnsupportedEncodingException {
		String value = URLEncoder.encode(token, "utf-8");
		Cookie cookie = new Cookie("token", value);
		cookie.setMaxAge(3600 * 24 * 5);// 设置其生命周期
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie中的token
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getTokenCookie(HttpServletRequest request) throws UnsupportedEncodingException {
		String token = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("token")) {
					token = cookie.getValue();
				}
			}
		}
		return URLDecoder.decode(token, "utf-8");
	}

	/***
	 * 获取浏览器IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		return ip;
	}
	
	/**
	 * 获取指定位数的随机数
	 * @param length
	 * @return
	 */
	public static String getRandom(int length) {
		Random r = new Random();
		if (length < 1){
			return "";
		}
		if (length == 1){
			return "" + r.nextInt(10);
		}
		int start = (int)Math.pow(10, length-1);
		int end = (int)Math.pow(10, length);
		return "" + (r.nextInt(end - start) + start);
	}
	
	/**
	 * 多字符串拼接
	 * @param strings
	 * @return StringBuffer
	 */
	public static StringBuffer getBuffer(Object... strings) {
		StringBuffer buf = new StringBuffer();
		Arrays.stream(strings).map(str -> buf.append(str)).collect(Collectors.toList());
		return buf;
	}
	
	/**
	 * 多字符串拼接
	 * @param strings
	 * @return String
	 */
	public static String getBufferString(Object... strings) {
		return getBuffer(strings).toString();
	}
	
	/**
	 * 域名请求协议检测—默认添加Http
	 */
	public static String getWholeDomainName(String domainName) {
		if (StringUtils.isEmpty(domainName))
			return "";
		String schema = "";
		int index = domainName.indexOf("://");
		if (index == 4 || index == 5) {
			schema = domainName.substring(0, index);
		}
		if ("http".equalsIgnoreCase(schema) 
				|| "https".equalsIgnoreCase(schema)) {
			schema = "";
		}else{
			schema = "http://";
		}
		return getBufferString(schema,domainName);
	}
	
	/**
	 * 千位分割：99999 = 99,999.00
	 * @param obj
	 * @return
	 */
	public static String thousandsString(Object obj) {
		StringBuffer buf = new StringBuffer();
		String str = String.valueOf(obj);
		try {
			String s = String.format("%.2f", Double.valueOf(str));
			String[] arr = s.split("\\.");
			StringBuffer num = new StringBuffer(arr[0]);
			while (num.length() > 3) {
				int length = num.length();
				buf.append(",").append(num.substring(length-3));
				num = new StringBuffer(num.substring(0,length-3));
			}
			return getBuffer(num,buf,".",arr[1]).toString();
		} catch (Exception e) {
			return str;
		}
	}
	
	public static String getInputStreamParam(HttpServletRequest request){
        String body = "";
        try {
            ServletInputStream inputStream = request.getInputStream(); 
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while(true){
                String info = br.readLine();
                if(info == null){
                    break;
                }
                if(body == null || "".equals(body)){
                    body = info;
                }else{
                    body += info;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }            
        return body;
    }
}
