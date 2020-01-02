package com.iwanol.paypal.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.security.SecurityManager;

public class AjaxAuthFailHandler extends SimpleUrlAuthenticationFailureHandler{
	@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		Object locked = SecurityManager.getValueFromSession("locked");
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject ob = new JSONObject();
		if(!StringUtils.isEmpty(locked)){
			ob.put("code","401");
			ob.put("msg","账户已被禁用,请联系管理员");
			SecurityManager.getSession().removeAttribute("locked");
		}else{
			ob.put("code","401");
			ob.put("msg","用户名或密码错误！");
		}
		out.print(ob.toString());
		out.flush();
		out.close();
    }
}
