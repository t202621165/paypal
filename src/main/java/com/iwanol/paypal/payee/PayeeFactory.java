package com.iwanol.paypal.payee;

import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.impl.Pay_Alipay;
import com.iwanol.paypal.payee.impl.Pay_Hangtian;
import com.iwanol.paypal.payee.impl.Pay_HuiJu;
import com.iwanol.paypal.payee.impl.Pay_LongBao;
import com.iwanol.paypal.payee.impl.Pay_ShuangQian;
import com.iwanol.paypal.payee.impl.Pay_ZhangLing;
import com.iwanol.paypal.payee.impl.Pay_ZhangLing_new;

public class PayeeFactory {

	public static Pay_Payee getInstance(Payee payee) {
		if (payee.getType())
			return new Pay_Alipay();
		else{
			if (payee.getMark().contains("huiju")){
				return new Pay_HuiJu();
			}
			
			if (payee.getMark().contains("longbao")){
				return new Pay_LongBao();
			}
			
			if (payee.getMark().contains("hangtian")){
				return new Pay_Hangtian();
			}
			
			if (payee.getMark().contains("zhanglingnew")){
				return new Pay_ZhangLing_new();
			}
			
			if (payee.getMark().contains("zhangling")){
				return new Pay_ZhangLing();
			}
			
		}
		return new Pay_ShuangQian();
	}
}
