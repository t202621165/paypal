package com.iwanol.paypal.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(value = MyException.class)
    @ResponseBody
    public ErrorInfo<String> jsonErrorHandler(HttpServletRequest req, MyException e) throws Exception {
		String[] array = e.getMessage().split(",");
        ErrorInfo<String> r = new ErrorInfo<>();
        r.setMessage(array[1]);
        r.setCode(Integer.valueOf(array[0]));
        r.setData("Some Data");
        r.setUrl(req.getRequestURL().toString());
        return r;
    }

}
