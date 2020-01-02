package com.iwanol.paypal.third.hangtian;


import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

public class PPCrypto{

  public static String base64EncodeStr(byte[] data) {
    return new String(Base64.encode(data));
  }

  public static byte[] base64StrDecode(String str) {
      String tmp = new String(str);
      tmp = tmp.replaceAll(" ", "+");
      return Base64.decode(tmp.getBytes());
  }

  public static byte[] md5Hash(byte[] data)throws Exception{
    MessageDigest md = MessageDigest.getInstance("MD5");
    return md.digest(data);
  }

  public static byte[] sha1Hash(byte[] data) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA1");
    return md.digest(data);
  }

  public static byte[] des3EcbDec(byte[] data, byte[] key)throws Exception{
      Key k = initKey3Des(key);
      Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding", "BC");
      cipher.init(2, k);
      return cipher.doFinal(data);
  }

  public static byte[] des3EcbEnc(byte[] data, byte[] key) throws Exception {
      Key k = initKey3Des(key);
      Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding", "BC");
      cipher.init(1, k);
      return cipher.doFinal(data);
  }

  private static Key initKey3Des(byte[] key) throws Exception {
      byte[] inkey = key;
      if (16 == key.length) {
          inkey = new byte[24];
          System.arraycopy(key, 0, inkey, 0, 16);
          System.arraycopy(key, 0, inkey, 16, 8);
      }

      DESedeKeySpec dks = new DESedeKeySpec(inkey);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede", "BC");
      return keyFactory.generateSecret(dks);
  }

  public static byte[] aesEcbDec(byte[] data, byte[] key)throws Exception {
      Key k = initKeyAes(key);

      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(2, k);
      return cipher.doFinal(data);
  }

  public static byte[] aesEcbEnc(byte[] data, byte[] key) throws Exception {
      Key k = initKeyAes(key);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(1, k);
      return cipher.doFinal(data);
  }

  private static Key initKeyAes(byte[] key) throws Exception {
      SecretKey secretKey = new SecretKeySpec(key, "AES");
      return secretKey;
  }

  public static byte[] genKey(int keylen) {
    SecureRandom random = new SecureRandom();
    byte[] bytes = new byte[keylen];
    random.nextBytes(bytes);
    return bytes;
  }

}