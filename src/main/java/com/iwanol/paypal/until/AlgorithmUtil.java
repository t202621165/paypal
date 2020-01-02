package com.iwanol.paypal.until;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AlgorithmUtil {
	
	private static final String AES = "AES";

    /**
     * 随机生成秘钥
     */
    public static String getKey() {
        return AlgorithmUtil.getKey(128);
    }

    private static String getKey(int number) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(AES);
            kg.init(number);// 要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            return Base64.encodeBase64String(b);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成AES秘钥串失败", e);
        }
    }

    public static String encode(String aesKey, String plaintext) {
        try {
            // 根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(Base64.decodeBase64(aesKey), AES);
            // 根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance(AES);
            // 初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeBase64String(cipher.doFinal(plaintext.getBytes("UTF-8")));
        } catch (Throwable e) {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    public static String decode(String aesKey, String ciphertext) {
        try {
            // 根据字节数组生成AES密钥
            SecretKey key = new SecretKeySpec(Base64.decodeBase64(aesKey), AES);
            // 根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance(AES);
            // 初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.decodeBase64(ciphertext)), "UTF-8");
        } catch (Throwable e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

}
