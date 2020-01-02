package com.iwanol.paypal.until;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SignUtils {
	/**
	 * 校验签名
	 * @param requestMap
	 * @param merPublicCertPath
	 * @param merPublicKey
	 * @return
	 */
	public static  Map<String,String>  checkSign(Map<String,Object> requestMap,String  merPublicCertPath,String signType) {
		Map<String,String> returnMap=new HashMap<String, String>();
		try {
			String mer_sign = (String)requestMap.get(signType);//商户签名密文
			String checkSign =SignUtils.getWaitSign(requestMap);//平台拼接签名字符串
			
			boolean flag=CertificateUtils.verifyMsg(mer_sign, checkSign, merPublicCertPath);
			if(flag){
				returnMap.put("signFlag", "success");
			}else{
				returnMap.put("signFlag", "fail");
				returnMap.put("remark", "验签失败");
			}
			return returnMap;
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("signFlag", "fail");
			returnMap.put("remark", "验签异常");
			return returnMap;
		}
	}
	
	/**
	 * 响应、异步通知 生成签名
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public static  String  generateSign(Map<String,String> requestMap,String priKey,String pwd) throws Exception  {
		String checkSign =SignUtils.getWaitSign_(requestMap);//平台拼接签名字符串
		return CertificateUtils.signMsg(checkSign, priKey, pwd, "UTF-8");
	}
	
	/**
	 * 响应、异步通知 生成签名
	 * @param requestMap
	 * @return
	 * @throws Exception
	 */
	public static  String  generateSign_(Map<String,Object> requestMap,String priKey,String pwd) throws Exception  {
		String checkSign =SignUtils.getWaitSign(requestMap);//平台拼接签名字符串
		return CertificateUtils.signMsg(checkSign, priKey, pwd, "UTF-8");
	}
	
	//签名
	private static String getWaitSign(Map<String, Object> map){
		Object[] keys =  map.keySet().toArray();
		Arrays.sort(keys);
		StringBuilder originStr = new StringBuilder();
		for(Object key:keys){
			if(null!=map.get(key)&&!map.get(key).toString().equals("")&&!"sign".equals(key)&&!"signType".equals(key)&&!"signature".equals(key))
			originStr.append(key).append("=").append(map.get(key)).append("&");
		}
		String str=originStr.toString();
		return str.substring(0, str.length()-1);
	}
	
	private static String getWaitSign_(Map<String, String> map){
		Object[] keys =  map.keySet().toArray();
		Arrays.sort(keys);
		StringBuilder originStr = new StringBuilder();
		for(Object key:keys){
			if(null!=map.get(key)&&!map.get(key).toString().equals("")&&!"sign".equals(key)&&!"signType".equals(key)&&!"signature".equals(key))
			originStr.append(key).append("=").append(map.get(key)).append("&");
		}
		String str=originStr.toString();
		return str.substring(0, str.length()-1);
	}
}
