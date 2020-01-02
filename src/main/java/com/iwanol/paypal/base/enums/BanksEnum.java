package com.iwanol.paypal.base.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 银行枚举类
 * @author International
 * 2018年7月07日 下午5:20:20
 */
public enum BanksEnum {
	
	/**支付宝*/ALIPAY("支付宝"),/**中国银行*/BOC("中国银行"),/**中国农业银行*/ABC("农业银行"),
	
	/**中国建设银行*/CCB("建设银行"),/**中国工商银行*/ICBC("工商银行"),/**招商银行*/CMB("招商银行"),
	
	/**交通银行*/COMM("交通银行"),/**中国民生银行*/CMBC("民生银行"),/**中国邮政储蓄银行*/PSBC("邮政储蓄"),
	
	/**中信银行*/CITIC("中信银行"),/**广大银行*/CEB("光大银行"),/**兴业银行*/CIB("兴业银行"),
	
	/**平安银行*/PINGAN("平安银行"),/**广州银行*/GZCB("广州银行"),/**广州发展银行*/GDB("广发银行"),
	
	/**北京银行*/BOB("北京银行"),/**上海银行*/BOSC("上海银行"),/**上海浦东发展银行*/SPDB("浦发银行"),HXB("华夏银行");
	
	/**银行名称*/
	private String name;
	
	private BanksEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * 根据标识判断银行是否存在
	 * @param mark
	 * @return
	 */
	public static boolean isPresent(String mark) {
		for (BanksEnum bank : BanksEnum.values()) {
			if (bank.toString().equalsIgnoreCase(mark))
				return true;
		}
		return false;
	}
	
	/**
	 * 根据银行标识获取银行名称
	 * @param mark
	 * @return
	 */
	public static String getBankNameByMark(String mark) {
		for (BanksEnum bank : BanksEnum.values()) {
			if (bank.toString().equalsIgnoreCase(mark))
				return bank.getName();
		}
		return "";
	}
	
	/**
	 * 根据银行名称获取银行标识
	 * @param mark
	 * @return
	 */
	public static String getBankMarkByName(String name) {
		for (BanksEnum bank : BanksEnum.values()) {
			if (bank.getName().equalsIgnoreCase(name))
				return bank.toString();
		}
		return "";
	}
	
	/**
	 * 将枚举类转换为JSONArray
	 * @return
	 */
	public static JSONArray toJSONArray() {
		JSONArray array = new JSONArray();
		for (BanksEnum bank : BanksEnum.values()) {
			array.add(bank.toJSONObject());
		}
		return array;
	}
	
	/**
	 * 将枚举对象转换成JSONObject
	 * @return
	 */
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("mark", toString());
		obj.put("name", getName());
		return obj;
	}
	
}
