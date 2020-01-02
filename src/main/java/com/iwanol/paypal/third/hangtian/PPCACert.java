package com.iwanol.paypal.third.hangtian;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

public class PPCACert{
  public static KeyStore getKeyStoreByPath(String keyStorePath, String password)
      throws Exception  {
      KeyStore ks = KeyStore.getInstance("PKCS12");
      FileInputStream is = new FileInputStream(keyStorePath);
      ks.load(is, password.toCharArray());
      is.close();
      return ks;
  }

  public static Certificate getCertificateByStr(String szCertificate) throws Exception {
      byte[] certBytes = PPCrypto.base64StrDecode(szCertificate);
      ByteArrayInputStream bais = new ByteArrayInputStream(certBytes);
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      Certificate certificate = certificateFactory.generateCertificate(bais);
      bais.close();
      return certificate;
  }

  public static Certificate getCertificateByPath(String certificatePath) throws Exception {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      FileInputStream in = new FileInputStream(certificatePath);
      Certificate certificate = certificateFactory.generateCertificate(in);
      in.close();
      return certificate;
  }


  public static Certificate getCertificateByStore(String keyStorePath, String alias, String password) throws Exception {
      KeyStore ks = getKeyStoreByPath(keyStorePath, password);
      return ks.getCertificate(alias);
  }


  public static PrivateKey getPrivateKeyByStore(String keyStorePath, String alias, String password) throws Exception {
      KeyStore ks = getKeyStoreByPath(keyStorePath, password);
      return (PrivateKey)ks.getKey(alias, password.toCharArray());
  }

  public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath, String password, String alias) throws Exception {
      PrivateKey privateKey = getPrivateKeyByStore(keyStorePath, alias, password);
      Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
      cipher.init(2, privateKey);
      return cipher.doFinal(data);
  }

  public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privKey) throws Exception {
      Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
      cipher.init(2, privKey);
      return cipher.doFinal(data);
  }

  public static byte[] encryptByPublicKey(byte[] data, Certificate cert) throws Exception {
      PublicKey publickey = cert.getPublicKey();
      Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
      cipher.init(Cipher.ENCRYPT_MODE, publickey);
      return cipher.doFinal(data);
  }

  public static byte[] sign(byte[] sign, PrivateKey privkey, String signAlg) throws Exception {
	  Signature signature = Signature.getInstance(signAlg);
	  signature.initSign(privkey);
	  signature.update(sign);
	  return signature.sign();
  }
  
  public static byte[] sign(String filePath, PrivateKey privkey, String signAlg) throws Exception {
      InputStream is = new FileInputStream(filePath);
      Signature signature = Signature.getInstance(signAlg);
      signature.initSign(privkey);
      byte[] buf = new byte[1024];
      int buflen = 0;
      while ((buflen = is.read(buf)) > 0) {
          signature.update(buf, 0, buflen);
      }
      is.close();
      return signature.sign();
  }

  public static boolean verify(byte[] data, byte[] sign, X509Certificate certificate) throws Exception {
	  Signature signature = Signature.getInstance(certificate.getSigAlgName());
	  signature.initVerify(certificate);
	  signature.update(data);
	  return signature.verify(sign);
  }
  
  public static boolean verify(String filePath, byte[] sign, X509Certificate certificate) throws Exception {
      InputStream is = new FileInputStream(filePath);
      Signature signature = Signature.getInstance(certificate.getSigAlgName());
      signature.initVerify(certificate);
      byte[] buf = new byte[1024];
      int buflen = 0;
      while ((buflen = is.read(buf)) > 0) {
          signature.update(buf, 0, buflen);
      }
      is.close();
      return signature.verify(sign);
  }
}