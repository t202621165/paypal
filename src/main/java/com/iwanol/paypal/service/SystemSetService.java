package com.iwanol.paypal.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.SystemSet;
import com.iwanol.paypal.repository.SystemSetRepository;
import com.iwanol.paypal.until.SMS;

@Service
public class SystemSetService extends BaseServiceImpl<SystemSet, SystemSet>{
	
	@Value("${com.paypal.mark}")
	private String mark;
	
	@Autowired
	private SystemSetRepository systemSetRepository;

	@Override
	public BaseRepository<SystemSet, Long> getRepository() {
		return systemSetRepository;
	}

	@Override
	public boolean addValidate(SystemSet t) {
		return false;
	}

	@Override
	public Specification<SystemSet> getSpecification(SystemSet v) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 系统资金自提短信验证码获取
	 * @return
	 */
	public ReturnMsgEnum getSMSCode(SMS sms) {
		if (sms.sendValidate()) {
			SystemSet system = findByMark();
			if (system == null || system.getMerchant() == null)
				return ReturnMsgEnum.error.setMsg("无法获取验证码，请前往完善系统设置！");
			String phoneNumber = system.getMerchant().getTelPhone();
			sms.init(phoneNumber);
			return sms.sender();
		}
		return ReturnMsgEnum.returnInstance();
	}
	
	public SystemSet findByMark() {
		Optional<SystemSet> optional = systemSetRepository.findByMark(mark);
		if (optional.isPresent())
			return optional.get();
		return null;
	}

	/**
	 * 获取网站域名和版权信息
	 * @return
	 */
	//@Cacheable(key="'findWebCopyRight'",cacheNames = "systemSet")
	public SystemSet findWebCopyRight() {
		Optional<SystemSet> optional = systemSetRepository.find();
		if (optional.isPresent())
			return optional.get();
		return null;
	}

	//@CacheEvict(allEntries = true,cacheNames = "systemSet")
	public JSONObject updateLogState(boolean logState) {
		systemSetRepository.updateLogState(mark);
		return ReturnMsgEnum.success.setMsg(logState?"已开启系统日志监控！":"已关闭系统日志监控！").toJson();
	}

	//@CacheEvict(allEntries = true,cacheNames = "systemSet")
	public JSONObject updateRouteState(boolean routeState) {
		systemSetRepository.updateRouteState(mark);
		return ReturnMsgEnum.success.setMsg(routeState?"已开启通道轮循！":"已关闭通道轮循！").toJson();
	}

	public JSONObject updateTailAmountScope(BigDecimal scope){
		int i = systemSetRepository.updateTailAmountScope(scope, mark);
		if (i > 0)
			return ReturnMsgEnum.success.setMsg("风控范围设置成功").toJson();
		return ReturnMsgEnum.error.setMsg("风控范围设置失败").toJson();
	}
}
