package com.iwanol.paypal.until;

import java.io.UnsupportedEncodingException;

import org.springframework.util.DigestUtils;

/**
 * MD5签名、验签工具类
 * @author Administrator
 *
 */
public class MD5Util {
	
	/**
	 * MD5签名-小写
	 * 
	 * @param content
	 *            需要签名的字符串
	 * @param key
	 *            密钥
	 * @param charset
	 *            编码格式
	 * @return 签名结果
	 */
	public static String sign(String content, String key, String charset) {
		content = content + key;
		return DigestUtils.md5DigestAsHex(getContentBytes(content, charset));
	}
	
	/**
	 * MD5签名验证
	 * 
	 * @param content
	 *            验证字符串
	 * @param sign
	 *            待验证签名
	 * @param key
	 *            密钥
	 * @param charset
	 *            编码格式
	 * @return 验证结果
	 */
	public static boolean verify(String content, String sign, String key, String charset) {
		content = content + key;
		String mysign = DigestUtils.md5DigestAsHex(getContentBytes(content, charset));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 将签名串转换成Byte数组
	 * @param content
	 * @param charset
	 * @return
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:"
					+ charset);
		}
	}

}
