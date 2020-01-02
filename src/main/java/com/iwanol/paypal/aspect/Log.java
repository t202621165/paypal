package com.iwanol.paypal.aspect;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwanol.paypal.security.SecurityManager;
import com.iwanol.paypal.until.CommonUtil;

/**
 * aop 实现请求日志处理
 * @author leo
 *
 */
@Aspect
@Component
public class Log {
	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	
	@Pointcut("execution(* com.iwanol.paypal.controller.*.*(..))")
	public void log(){}
	
	@Before("log()")
	public void doControllerBefore(JoinPoint joinPoint){
		// 接收到请求，记录请求内容
        logger.info("Log.doBefore()");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        logger.info("管理员 : " + SecurityManager.getUserName());
        logger.info("请求路径 : " + request.getRequestURI());
        logger.info("请求方式 : " + request.getMethod());
        logger.info("请求IP : " + CommonUtil.getIpAddr(request));
        logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
	}
}
