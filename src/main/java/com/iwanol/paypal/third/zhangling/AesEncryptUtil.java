package com.iwanol.paypal.third.zhangling;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AesEncryptUtil {

	 public static String aesEncrypt(String str, String key) throws Exception {
	        if (str == null || key == null) return null;
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
	        byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
	        return Base64.encodeBase64String(bytes);
    }

    public static String aesDecrypt(String str, String key) throws Exception {
        if (str == null || key == null) return null;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = Base64.decodeBase64(str);
        bytes = cipher.doFinal(bytes);
        return new String(bytes, "utf-8");
    }
	 public static void main(String[] args) throws Exception {
		String a="奥斯卡";
		String key="a683aaa5ed68c97b";//必须为16位
		String b=AesEncryptUtil.aesEncrypt(a, key);
		System.out.println(b);
		System.out.println(AesEncryptUtil.aesDecrypt(b, key));
	 }
}
