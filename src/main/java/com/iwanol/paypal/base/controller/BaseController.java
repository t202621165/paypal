package com.iwanol.paypal.base.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController<T,VO> {
	
	public abstract Map<String, Object> pagingList(VO vo,HttpServletRequest request);
}
