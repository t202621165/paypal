package com.iwanol.paypal.third.hangtian;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class PPSecurity{
    private String m_CertPath;
    private String m_Alias;
    private X509Certificate m_cert;
    private PrivateKey m_privkey;
    @SuppressWarnings("unused")
	private String m_priPass;
    private String m_privKeyPath;
    private byte[] m_sensitiveKey;
    private byte[] m_fileKey;

    private static final int AES_KEY_LEN = 16;
    private int RSA_BYTE_NUM_PUB = 256;
    private int RSA_BYTE_NUM_PRI = 256;

    /**初始化秘钥信息*/
    public PPSecurity(String ppCertPath, String merPriKeyPath, String priAlias, String passwd, String hexSensitiveKey, String hexFileKey) throws Exception{
        Security.addProvider(new BouncyCastleProvider());
        this.m_CertPath = ppCertPath;
        this.m_cert = (X509Certificate) PPCACert.getCertificateByPath(this.m_CertPath);
        this.RSA_BYTE_NUM_PUB = ((RSAPublicKey)this.m_cert.getPublicKey()).getModulus().bitLength() / 8;
        this.m_Alias = priAlias;
        this.m_priPass = passwd;
        this.m_privKeyPath = merPriKeyPath;
        this.m_privkey = PPCACert.getPrivateKeyByStore(this.m_privKeyPath, this.m_Alias, passwd);
        this.RSA_BYTE_NUM_PRI = ((RSAPrivateKey)this.m_privkey).getModulus().bitLength() / 8;
        this.m_sensitiveKey = Hex.decode(hexSensitiveKey);
        this.m_fileKey = Hex.decode(hexFileKey);
    }
    public PPSecurity(){
    super();
    }
    public byte[] PPFCommDataEncode(byte[] inData) throws Exception {
        byte[] rkey = PPCrypto.genKey(AES_KEY_LEN);
        //encrypt random key by rsa
        byte[] keyEnc = PPCACert.encryptByPublicKey(rkey, this.m_cert);
        //sign random key by pri key
        System.out.println(this.m_cert.getSigAlgName());
        byte[] signKey = PPCACert.sign(inData, this.m_privkey, this.m_cert.getSigAlgName());
        //encrypt data by random key
        byte[] cryptedData = PPCrypto.aesEcbEnc(inData, rkey);
        byte[] output = new byte[keyEnc.length + signKey.length + cryptedData.length];
        System.arraycopy(keyEnc, 0, output, 0, keyEnc.length);
        System.arraycopy(signKey, 0, output, keyEnc.length, signKey.length);
        System.arraycopy(cryptedData, 0, output, keyEnc.length + signKey.length, cryptedData.length);
        return Base64.encode(output);
    }

    public byte[] PPFCommDataDecode(byte[] inData) throws Exception {
        inData = Base64.decode(inData);
        //get random key
        byte[] encKey = new byte[this.RSA_BYTE_NUM_PRI];
        System.arraycopy(inData, 0, encKey, 0, this.RSA_BYTE_NUM_PRI);
        byte[] aeskey = PPCACert.decryptByPrivateKey(encKey, this.m_privkey);
        //decrypt data by random key
        byte[] encryptedData = new byte[inData.length - this.RSA_BYTE_NUM_PRI - this.RSA_BYTE_NUM_PUB];
        System.arraycopy(inData, this.RSA_BYTE_NUM_PRI + this.RSA_BYTE_NUM_PUB, encryptedData, 0, inData.length - this.RSA_BYTE_NUM_PRI - this.RSA_BYTE_NUM_PUB);
        byte[] outData = PPCrypto.aesEcbDec(encryptedData, aeskey);
        //verify sign
        byte[] signData = new byte[this.RSA_BYTE_NUM_PUB];
        System.arraycopy(inData, this.RSA_BYTE_NUM_PRI, signData, 0, this.RSA_BYTE_NUM_PUB);
        if (!PPCACert.verify(outData, signData, this.m_cert)) {
            return null;
        }
        return outData;
    }
    public byte[] PPFCommDataForH5Encode(byte[] inData) throws Exception {
        byte[] signData = PPCACert.sign(inData, this.m_privkey, this.m_cert.getSigAlgName());
        byte[] outdata = new byte[signData.length + inData.length];
        System.arraycopy(signData, 0, outdata, 0, signData.length);
        System.arraycopy(inData, 0, outdata, signData.length, inData.length);
        return Base64.encode(outdata);
    }
    public byte[] PPFCommDataForH5Decode(byte[] inData) throws Exception {
        byte[] realData = Base64.decode(inData);
        byte[] signData = new byte[this.RSA_BYTE_NUM_PUB];
        System.arraycopy(realData, 0, signData, 0, signData.length);
        byte[] data = new byte[realData.length - signData.length];
        System.arraycopy(realData, signData.length, data, 0, realData.length - signData.length);
        boolean ret = PPCACert.verify(data, signData, this.m_cert);
        if (ret) {
            return data;
        } else {
            return null;
        }
    }
    public byte[] PPFEncryptSensitiveData(byte[] inData) throws Exception {
        return Hex.encode(PPCrypto.aesEcbEnc(inData, this.m_sensitiveKey));
    }

    public byte[] PPFDecryptSensitiveData(byte[] inData) throws Exception {
        return PPCrypto.aesEcbDec(Hex.decode(inData), this.m_sensitiveKey);
    }
  
    public boolean PPFEncryptFile(String InFilePath, String OutFilePath) throws Exception {
        Key k = new SecretKeySpec(this.m_fileKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        InputStream in = new FileInputStream(InFilePath);
        OutputStream out = new FileOutputStream(OutFilePath);

        byte[] signData = PPCACert.sign(InFilePath, this.m_privkey, this.m_cert.getSigAlgName());
        out.write(cipher.update(signData));

        byte[] buf = new byte[this.RSA_BYTE_NUM_PRI];
        byte[] tmp = new byte[this.RSA_BYTE_NUM_PRI];
        int tmplen = 0;
        int buflen = in.read(buf);
        while (buflen == this.RSA_BYTE_NUM_PRI) {
          if ((tmplen = in.read(tmp)) == this.RSA_BYTE_NUM_PRI) {
              out.write(cipher.update(buf, 0, buflen));
          } else {
              if (tmplen < 0) {
                  break;
              } else {
                  out.write(cipher.update(buf, 0, buflen));
                  System.arraycopy(tmp, 0, buf, 0, tmplen);
                  buflen = tmplen;
              }
          }
          System.arraycopy(tmp, 0, buf, 0, this.RSA_BYTE_NUM_PRI);
        }
        cipher.doFinal();
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        out.write(cipher.doFinal(buf, 0, buflen));
        in.close();
        out.close();
        return true;
    }
  
    public boolean PPFDecryptFile(String InFilePath, String OutFilePath) throws Exception {
        Key k = new SecretKeySpec(this.m_fileKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, k);
        InputStream in = new FileInputStream(InFilePath);
        OutputStream out = new FileOutputStream(OutFilePath);

        byte[] sign = new byte[this.RSA_BYTE_NUM_PUB];
        if (this.RSA_BYTE_NUM_PUB != in.read(sign)) {
          in.close();
          out.close();
          return false;
        }
        sign = cipher.update(sign);

        byte[] buf = new byte[this.RSA_BYTE_NUM_PUB];
        byte[] tmp = new byte[this.RSA_BYTE_NUM_PUB];
        int tmplen = 0;
        int buflen = in.read(buf);
        while (buflen == this.RSA_BYTE_NUM_PUB) {
          if ((tmplen = in.read(tmp)) == this.RSA_BYTE_NUM_PUB) {
              out.write(cipher.update(buf, 0, buflen));
          } else {
              if (tmplen < 0) {
                  break;
              } else {
                  out.write(cipher.update(buf, 0, buflen));
                  System.arraycopy(tmp, 0, buf, 0, tmplen);
                  buflen = tmplen;
              }
          }
          System.arraycopy(tmp, 0, buf, 0, this.RSA_BYTE_NUM_PUB);
        }
        cipher.doFinal();
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, k);
        out.write(cipher.doFinal(buf, 0, buflen));
        in.close();
        out.close();
        return PPCACert.verify(OutFilePath, sign, this.m_cert);
    }
}