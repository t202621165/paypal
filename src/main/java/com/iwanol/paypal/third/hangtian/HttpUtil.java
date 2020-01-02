package com.iwanol.paypal.third.hangtian;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by lifeng on 2018/11/20.
 */
public class HttpUtil {
    public static String request(String notifyReqDataUrl,boolean isPost) throws Exception{
        String msg = "";
        String serverURL = "";
        if (notifyReqDataUrl.contains("?")) {
            serverURL = notifyReqDataUrl.substring(0, notifyReqDataUrl.indexOf("?"));
            msg = notifyReqDataUrl.substring(notifyReqDataUrl.indexOf("?") + 1);
        } else {
            serverURL = notifyReqDataUrl;
        }
        return request(serverURL,msg,isPost);
    }

    /**
     * @exception Exception
     */
    public static String request(String serverURL, String msg,boolean isPost) throws Exception{
        String url = serverURL;
        if(!isPost){
            if (serverURL.indexOf("?") != -1)
                url = serverURL + "&" + msg;
            else
                url = serverURL + "?" + msg;
        }
        //使客户端信任服务器的证书
        boolean isSSL = url.toUpperCase().indexOf("HTTPS://") > -1;
        if (isSSL)
            setTrustSSLContent();
        java.net.URL aURL = null;
        java.net.HttpURLConnection urlConnection = null;
        try {
            aURL = new java.net.URL(url);
            urlConnection =(java.net.HttpURLConnection) aURL.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setRequestProperty("User-Agent", "MSIE");
            if(!isPost){
                System.out.println("请求url:"+url);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            }
            else {
                System.out.println("请求url:"+serverURL);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            urlConnection.connect();
            if(isPost){
                //数据发送
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                out.write(msg);
                out.flush();
                out.close();
            }
            int resCode = urlConnection.getResponseCode();
            if (resCode != 200) {
                throw new Exception("目标服务器异常:"+resCode);
            }
            int contentLen = urlConnection.getContentLength();
            java.io.DataInputStream in = new java.io.DataInputStream(urlConnection.getInputStream());

            if (contentLen <= 0)
                contentLen = 10240;

            byte buffer[] = new byte[contentLen];
            int off = 0;
            int len = 0;
            while (true) {
                len = in.read(buffer, off, contentLen - off);
                if (len == -1 || len == 0)
                    break;
                off = off + len;
                if (off >= contentLen)
                    break;
            }
            return new String(buffer, 0, off,"UTF-8");

        } catch (Exception e) {
            throw e;
        }finally{
            if(urlConnection!=null){
                try{
                    urlConnection.disconnect();
                    urlConnection = null;
                }catch(Exception e){

                }
            }
        }
    }


    /**
     *
     * @param serverURL
     * @param msg
     * @param charset
     */
    public static String request(String serverURL, String msg, String charset) throws Exception{
        String url = serverURL;
        if(msg!=null&&!"".equals(msg.trim()))        {
            if (serverURL.indexOf("?") != -1)
                url = url + "&" + msg;
            else
                url = url + "?" + msg;
        }
        //使客户端信任服务器的证书
        boolean isSSL = url.toUpperCase().indexOf("HTTPS://") > -1;
        if (isSSL)
            setTrustSSLContent();

        java.net.URL aURL = null;
        java.net.HttpURLConnection urlConnection = null;
        try {
            aURL = new java.net.URL(url);
            urlConnection =(java.net.HttpURLConnection) aURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(4000);
            urlConnection.setReadTimeout(4000);
            urlConnection.setRequestProperty("User-Agent", "MSIE");
            urlConnection.setRequestProperty("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            urlConnection.connect();
            int resCode = urlConnection.getResponseCode();
            if (resCode != 200) {
                throw new Exception("服务器返回ResponseCode"+resCode);
            }
            int contentLen = urlConnection.getContentLength();
            java.io.DataInputStream in = new java.io.DataInputStream(urlConnection.getInputStream());
            if (contentLen <= 0)
                contentLen = 10240;

            byte buffer[] = new byte[contentLen];
            int off = 0;
            int len = 0;
            while (true) {
                len = in.read(buffer, off, contentLen - off);
                if (len == -1 || len == 0)
                    break;
                off = off + len;
                if (off >= contentLen)
                    break;
            }
            return new String(buffer, 0, off, charset);
        } catch (Exception e) {
            throw e;
        }finally{
            //关闭连接
            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                    urlConnection = null;
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    /**
     * 使客户端信任服务器的证书--单向认证
     */
    private static void setTrustSSLContent() throws Exception {

        //  Create a trust manager that does not validate certificate chains:

        TrustManager[] trustAllCerts =new TrustManager[1];

        TrustManager tm = new miTM();

        trustAllCerts[0] = tm;

        //SSLContext sc = SSLContext.getInstance("SSL");
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, null);

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier hv = new HostnameVerifier(){
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public static class miTM implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

}