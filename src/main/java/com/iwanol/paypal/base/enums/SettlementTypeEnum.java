package com.iwanol.paypal.base.enums;

public enum SettlementTypeEnum {

	T0(0, "T+0结算"),T1(1, "T+1结算");
	
	private Integer type;
	
	private String desc;
	
	private SettlementTypeEnum(Integer type, String desc){
		this.type = type;
		this.desc = desc;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public static SettlementTypeEnum getEnumByType(Integer type) {
		for(SettlementTypeEnum typeEnum : SettlementTypeEnum.values()){
			if(type == typeEnum.getType()){
	    		return typeEnum;
			}
		}
	    return null;
	}
	
	public static String getDescByType(Integer type) {
		SettlementTypeEnum typeEnum = getEnumByType(type);
		if (typeEnum == null)
			return "";
		return typeEnum.desc;
	}
}
