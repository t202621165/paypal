package com.iwanol.paypal.base.enums;

import com.alibaba.fastjson.JSONObject;

public enum ReturnMsgEnum {

	success(true, "操作成功！"),error(false, "操作失败！");
	
	private boolean state;
	
	private String msg;
	
	public static boolean isSucc = true;
	
	private ReturnMsgEnum(boolean state,String msg) {
		this.state = state;
		this.msg = msg;
	}
	
	public boolean getState() {
		return state;
	}

	public String getMsg() {
		return msg;
	}

	public ReturnMsgEnum setMsg(String msg) {
		this.msg = msg;
		isSucc = this.state;
		return this;
	}
	
	public static JSONObject returnMsg() {
		if (isSucc)
			return success.toJson();
		return error.toJson();
	}
	
	public static ReturnMsgEnum returnInstance() {
		if (isSucc)
			return success;
		return error;
	}
	
	public JSONObject toJson() {
		JSONObject json = new JSONObject();
		json.put("state", this.state);
		json.put("msg", this.msg);
		return json;
	}
	
}
