package com.iwanol.paypal.third.zhangling;
import java.util.Arrays;
import java.util.Map;

public class SignUtil {
	//签名
		public static String doEncrypt(Map<String, Object> map,String privateKey) throws Exception {
			Object[] keys =  map.keySet().toArray();
			Arrays.sort(keys);
			StringBuilder originStr = new StringBuilder();
			for(Object key:keys){
				if(null!=map.get(key)&&!map.get(key).toString().equals("")&&!"signature".equals(key))
				originStr.append(key).append("=").append(map.get(key)).append("&");
			}
			String str=originStr.toString();
			str=str.substring(0, str.length()-1);
			System.out.println(str);
			return RSA.sign(str, privateKey, "utf-8");
		}
}
