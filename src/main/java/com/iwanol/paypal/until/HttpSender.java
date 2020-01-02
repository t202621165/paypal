package com.iwanol.paypal.until;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSender {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static HttpSender httpSender;
	
	public static HttpSender getHttpSender(){
		if(httpSender == null){
			return new HttpSender();
		}
		return httpSender;
	}

	public <v> String connection(String url, String method, Map<String, v> param) {
		StringBuilder builder = new StringBuilder();
		try {
			StringBuilder params = new StringBuilder();
			param.entrySet().forEach(action -> {
				String key = action.getKey();
				String value = action.getValue().toString();
				params.append(key);
				params.append("=");
				params.append(value);
				params.append("&");
			});
			if (params.length() > 0) {
				params.deleteCharAt(params.lastIndexOf("&"));
			}
			URL restServiceURL = new URL(url + (params.length() > 0 ? "?" + params.toString() : ""));
			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
			httpConnection.setRequestMethod(method);
			httpConnection.setRequestProperty("Accept", "application/json");
			if (method.equalsIgnoreCase("POST")) {
				httpConnection.setDoInput(true);				
			}
			if (httpConnection.getResponseCode() != 200) {
				logger.info("HTTP请求失败 错误码:"+httpConnection.getResponseCode());
				return "";
			}
			InputStream inStrm = httpConnection.getInputStream();
			byte[] b = new byte[1024];
			int length = -1;
			while ((length = inStrm.read(b)) != -1) {
				builder.append(new String(b, 0, length));
			}
		} catch (MalformedURLException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
		return builder.toString();
	}
	
	public String doHttpAndHttps(String url, String requestMessage)
			throws Exception {
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			urlConnection = openUrlConn(url);
			byte[] b = requestMessage.getBytes("utf-8");
			out = urlConnection.getOutputStream();
			out.write(b);
			out.flush();
			in = urlConnection.getInputStream();
			String res = getContents(in);
			return res;
		} catch (Exception ce) {
			ce.printStackTrace();
			throw ce;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				} finally {
					out = null;
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				} finally {
					in = null;
				}
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
				urlConnection = null;
			}
		}
	}
	
	public String getContents(InputStream in ) {
		BufferedReader bre = null;
		StringBuffer sb = new StringBuffer();
		String contents = "";
		try {
			bre = new BufferedReader(new InputStreamReader(in,"utf-8"));
			while ((contents = bre.readLine()) != null) {// 判断最后一行不存在，为空结束循环
				sb.append(contents);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bre!=null){
					bre.close();
				}
			} catch (Exception e2) {
			}
			try {
				if(in!=null){
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return sb.toString();
	}

	private HttpURLConnection openUrlConn(String url) throws Exception {
		URL httpurl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) httpurl
				.openConnection();
		try {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			connection.setDoOutput(true);
			connection.setConnectTimeout(60*10*1000);
			connection.setReadTimeout(60*10*1000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			return connection;
		} catch (Exception e) {
			throw new Exception("对方URL无法连接" + url, e);
		} finally {
			connection.disconnect();
			connection = null;
		}

	}
	
}
