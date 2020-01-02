package com.iwanol.paypal.exception;

public class MyException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MyException(Integer code,String message){
		super(String.valueOf(code)+","+message);
	}

}
