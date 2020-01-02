package com.iwanol.paypal.until;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class CertificateUtils {
	private static final String PKCS12 = "PKCS12";
    private static final String X_DOT_509 = "X.509";
    private static final String SHA256_WITH_RSA = "SHA256withRSA";

    /**
     * 公钥验签
     * @param ciphertext 私钥签名值
     * @param plaintext 原始数据
     * @param publicKeyPath 公钥证书路径
     * @return
     */
    public static boolean verifyMsg(String ciphertext, String plaintext, String publicKeyPath) {
        InputStream certfile = null;
        try {
            certfile = new FileInputStream(new File(publicKeyPath));
            CertificateFactory cf = CertificateFactory.getInstance(X_DOT_509);
            X509Certificate x509cert = (X509Certificate) cf.generateCertificate(certfile);
            RSAPublicKey pubkey = (RSAPublicKey) x509cert.getPublicKey();
            Signature verify = Signature.getInstance(SHA256_WITH_RSA);
            verify.initVerify(pubkey);
            verify.update(plaintext.getBytes("UTF-8"));
            if (verify.verify(Base64.decodeBase64(ciphertext))) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            throw new RuntimeException("验签失败");
        } finally {
            if (certfile != null)
                try {
                    certfile.close();
                } catch (Throwable e) {
                    // do nothing
                }
        }
    }

    /**
     * 私钥签名
     * @param plaintext 原始签名串
     * @param privateKeyPath 私钥证书路径
     * @param passWord 私钥证书密码
     * @param charset 字符集
     * @return
     */
    public static String signMsg(String plaintext, String privateKeyPath, String passWord, String charset) {
        try {
            InputStream keyFile = new FileInputStream(new File(privateKeyPath));
            KeyStore ks = KeyStore.getInstance(PKCS12);
            try {
                ks.load(keyFile, passWord.toCharArray());
            } catch (Throwable ex) {
                if (keyFile != null)
                    keyFile.close();
                throw new RuntimeException("加载私钥失败");
            }
            Enumeration<String> myEnum = ks.aliases();
            String keyAlias = null;
            RSAPrivateCrtKey prikey = null;
            /* IBM JDK必须使用While循环取最后一个别名，才能得到个人私钥别名 */
            while (myEnum.hasMoreElements()) {
                keyAlias = myEnum.nextElement();
                if (ks.isKeyEntry(keyAlias)) {
                    prikey = (RSAPrivateCrtKey) ks.getKey(keyAlias, passWord.toCharArray());
                    break;
                }
            }
            if (prikey == null) {
                throw new RuntimeException("没有找到匹配私钥");
            } else {
                Signature sign = Signature.getInstance(SHA256_WITH_RSA);
                sign.initSign(prikey);
                sign.update(plaintext.getBytes(charset));
                return Base64.encodeBase64String(sign.sign());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("签名失败");
        }
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param plaintext
     *            待加密的明文
     * @param publicKeyPath
     *            公钥证书地址
     * @return
     * @throws Exception
     */
    public static  String encryptByPublicKey(String plaintext, String publicKeyPath) {
        InputStream certfile = null;
        RSAPublicKey pubkey = null;
        X509Certificate x509cert = null;
        try {
            certfile = new FileInputStream(new File(publicKeyPath));
            CertificateFactory cf = CertificateFactory.getInstance(X_DOT_509);
            x509cert = (X509Certificate) cf.generateCertificate(certfile);
            pubkey = (RSAPublicKey) x509cert.getPublicKey();
        } catch (Throwable e) {
            throw new RuntimeException("获取公钥对象失败");
        } finally {
            if (certfile != null)
                try {
                    certfile.close();
                } catch (Throwable e) {
                    // do nothing
                }
        }
        try {
            // 对数据加密
            Cipher cipher = Cipher.getInstance(pubkey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubkey);
            return Base64.encodeBase64String(cipher.doFinal(plaintext.getBytes()));
        } catch (Throwable e) {
            throw new RuntimeException("公钥加密失败");
        }
    }

    /**
     * <P>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData
     *            已加密数据
     * @param privateKey
     *            私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String ciphertext, String privateKeyPath, String passWord) {
        InputStream keyFile = null;
        RSAPrivateCrtKey prikey = null;
        try {
            keyFile = new FileInputStream(new File(privateKeyPath));
            KeyStore ks = KeyStore.getInstance(PKCS12);
            ks.load(keyFile, passWord.toCharArray());
            Enumeration<String> myEnum = ks.aliases();
            String keyAlias = null;
            /* IBM JDK必须使用While循环取最后一个别名，才能得到个人私钥别名 */
            while (myEnum.hasMoreElements()) {
                keyAlias = myEnum.nextElement();
                if (ks.isKeyEntry(keyAlias)) {
                    prikey = (RSAPrivateCrtKey) ks.getKey(keyAlias, passWord.toCharArray());
                    break;
                }
            }
        } catch (Throwable ex) {
            throw new RuntimeException("加载私钥失败");
        } finally {
            if (keyFile != null)
                try {
                    keyFile.close();
                } catch (IOException e) {
                    // do nothing
                }
        }
        try {
            Cipher cipher = Cipher.getInstance(prikey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, prikey);
            return new String(cipher.doFinal(Base64.decodeBase64(ciphertext)), "UTF-8");
        } catch (Throwable e) {
            throw new RuntimeException("私钥解密失败");
        }
    }
}
