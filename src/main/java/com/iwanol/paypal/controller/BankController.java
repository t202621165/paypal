package com.iwanol.paypal.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.service.BankService;
import com.iwanol.paypal.until.PageData;
import com.iwanol.paypal.vo.BankVo;

@RestController
@RequestMapping(value = "bank", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('profile.html')")
public class BankController {
	
	@Autowired
	private BankService bankService;
	
	@ModelAttribute
	public void getEntity(@RequestParam(value="id", required=false) Long id, 
			Map<String, Object> map, HttpServletRequest request) {
		if (id != null && "/bank/edit".equals(request.getRequestURI())) {
			map.put("bank", bankService.findEntity(id));
		}
	}

	@PostMapping(value = "/list")
	public Map<String, Object> list(PageData pageData, BankVo v){
		return bankService.findEntityBySpec(pageData, v);
	}
	
	@PostMapping(value = "/add")
	public JSONObject addBank(Bank bank){
		return bankService.saveEntity(bank);
	}
	
	@PostMapping(value = "/edit")
	public JSONObject updateBank(@ModelAttribute("bank") Bank bank){
		return bankService.updateEntity(bank, bank.getId() == null);
	}
	
	@PostMapping(value = "/delete")
	public JSONObject deleteBank(Bank bank){
		return bankService.deleteEntity(bank.getId());
	}
	
	/**
	 * 设置当前银行卡为主卡
	 * @param bank
	 * @return
	 */
	@PostMapping(value = "/type")
	public JSONObject updateBankType(Bank bank){
		return bankService.updateBankType(bank);
	}
	
	/**
	 * 开启/关闭银行卡代付功能
	 * @param bank
	 * @return
	 */
	@PostMapping(value = "/state")
	public JSONObject updateBankPayeeState(Bank bank){
		return bankService.updateBankPayeeState(bank);
	}
}
