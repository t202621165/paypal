package com.iwanol.paypal.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.iwanol.paypal.security.SecurityManager;

/**
 * 系统管理员登录日志切面
 * @author Administrator
 * 2018年6月26日 下午2:50:36
 */
@Aspect
@Component
public class LoginLog {

	@Pointcut(value = "@annotation(com.iwanol.paypal.aspect.annotation.Login)")
	public void login(){}
	
	@Before("login()")
	private void deBefore(JoinPoint joinPoint) throws Throwable {
		System.out.println("用户请求登录...");
	}
	
	@After("login()")
	private void after(JoinPoint jp) {
		System.out.println("管理员："+SecurityManager.getUserName()+" 登录成功！");
	}
}
