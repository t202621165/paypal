package com.iwanol.paypal.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.alibaba.fastjson.JSONObject;

public class AjaxAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		System.out.println("身份验证成功");
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject ob = new JSONObject();
		ob.put("code","200");
		ob.put("msg","身份验证成功");
		ob.put("url","/index.html");
		out.print(ob.toString());
		out.flush();
		out.close();	
    }
}
