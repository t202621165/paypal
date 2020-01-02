package com.iwanol.paypal.controller;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.service.MerchantService;
import com.iwanol.paypal.service.PayeeService;
import com.iwanol.paypal.service.SettleMentService;
import com.iwanol.paypal.until.CommonUtil;
import com.iwanol.paypal.until.DateUtil;
import com.iwanol.paypal.until.MD5Util;
import com.iwanol.paypal.until.PageData;
import com.iwanol.paypal.until.SMS;
import com.iwanol.paypal.until.ToolUtil;
import com.iwanol.paypal.vo.MerchantPayeeVo;
import com.iwanol.paypal.vo.PayeeVo;

import cn.hutool.http.HttpRequest;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

@RestController
@RequestMapping(value = "payee", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('system.html')")
public class PayeeController {

	@Autowired
	private PayeeService payeeService;
	@Autowired
	private MerchantService merchantService;
	@Autowired
	private SettleMentService settleMentService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@ModelAttribute
	public void getEntity(@RequestParam(value = "id", required = false) Long id, Map<String, Object> map,
			HttpServletRequest request) {
		if ("/payee/edit".equals(request.getRequestURI()))
			map.put("payee", payeeService.findEntity(id));
	}

	/**
	 * 代付通道列表
	 * 
	 * @param vo
	 * @return
	 */
	@PostMapping("/list")
	public Map<String, Object> payeeList(PageData pageData) {
		return payeeService.findEntityByPage(pageData);
	}

	/**
	 * 添加代付通道
	 * 
	 * @param vo
	 * @return
	 */
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('payee/add')")
	public JSONObject addPayee(Payee payee) {
		payee.init();
		return payeeService.saveEntity(payee);
	}

	/**
	 * 删除代付通道
	 * 
	 * @param vo
	 * @return
	 */
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('payee/delete')")
	public JSONObject deletePayee(Payee payee) {
		return payeeService.deleteEntity(payee.getId());
	}

	/**
	 * 编辑代付通道
	 * 
	 * @param vo
	 * @return
	 */
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('payee/edit')")
	public JSONObject updatePayee(@ModelAttribute("payee") Payee payee) {
		return payeeService.updateEntity(payee, payee.getId() == null);
	}

	/**
	 * 设置默认代付通道
	 * 
	 * @param vo
	 * @return
	 */
	@PostMapping("/default")
	@PreAuthorize("hasAuthority('payee/edit')")
	public JSONObject updatePayeeIsDefault(Payee payee) {
		return payeeService.updateDefault(payee);
	}

	/**
	 * 资金代付-支付宝
	 * 
	 * @param vo
	 * @return
	 */
	@PostMapping("/pay")
	@PreAuthorize("hasAuthority('payee/pay')")
	public JSONObject payee(PayeeVo vo) {
		return payeeService.payeeValidate(vo);
	}

	@PostMapping("/merchant/pay")
	@PreAuthorize("hasAuthority('payee/pay')")
	public JSONObject merPayee(MerchantPayeeVo vo) throws UnsupportedEncodingException {
		Optional<Merchant> optional = merchantService.findByAccount(vo.getMerchantId());
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("代付商户不存在").toJson();
		JSONObject result = vo.valid(optional.get());
		if (result.getBooleanValue("state")) {
			Map<String, String> param = new HashMap<String, String>();
			String orderNumber = "DF".concat(DateUtil.getDate("yyMMddHHmmssSSS")).concat(CommonUtil.getRandom(6));
			param.put("merchant_id", vo.getMerchantId());
			param.put("merchant_order", orderNumber);
			param.put("total_amount", vo.getTotalAmount());
			param.put("bank_code", vo.getBankCode());
			param.put("real_name", vo.getRealName());
			param.put("bank_name", vo.getBankName());
			param.put("bank_number", vo.getBankNumber());
			String signStr = ToolUtil.getToolUtil().formatUrlMap(param, null, true, false, true);
			String sign = MD5Util.sign(signStr, optional.get().getMerchantKey(), "utf-8");
			param.put("sign", sign);
			param.put("real_name", URLEncoder.encode(param.get("real_name"), "utf-8"));
			param.put("bank_name", URLEncoder.encode(param.get("bank_name"), "utf-8"));
			String body = ToolUtil.getToolUtil().formatUrlMap(param, null, false, false, true);
			String response = HttpRequest.get(vo.getUrl()).body(body).execute().body();
			logger.info("代付结果响应:" + response);
			JSONObject respObj = JSONObject.parseObject(response);
			if (respObj.getBooleanValue("state")) {
				return ReturnMsgEnum.success.setMsg(respObj.getString("msg").concat(",请前往提现业务中查看代付状态")).toJson();
			} else {
				return ReturnMsgEnum.error.setMsg(respObj.getString("msg").concat(",请前往提现业务中查看代付状态")).toJson();
			}
		}
		return result;
	}

	/**
	 * 审核通过代付
	 * 
	 * @param SettleMents
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@PostMapping("audit/go")
	@PreAuthorize("hasAuthority('payee/pay')")
	public JSONObject payee(@RequestBody List<SettleMent> SettleMents) throws UnsupportedEncodingException {
		//String url = "http://localhost:8083/tx/payee";
		 String url = "http://api.qyiol.com/tx/payee";
		List<SettleMent> list = settleMentService.findWaitAuditPayeeRecorde(SettleMents);
		if (!list.isEmpty()) {
			int i = 0;
			if (!list.isEmpty())
				for (SettleMent vo : list) {
					Map<String, String> param = new HashMap<String, String>();
					param.put("merchant_id", vo.getMerAccount());
					param.put("merchant_order", vo.getSerialNumber());
					param.put("total_amount", String.format("%.2f", vo.getAmount().doubleValue()));
					param.put("bank_code", vo.getBankMark());
					param.put("real_name", vo.getRealName());
					param.put("bank_name", vo.getBankName());
					param.put("bank_number", vo.getBankNumber());
					String signStr = ToolUtil.getToolUtil().formatUrlMap(param, null, true, false, true);
					String sign = MD5Util.sign(signStr, vo.getSecurityKey(), "utf-8");
					param.put("sign", sign);
					param.put("real_name", URLEncoder.encode(param.get("real_name"), "utf-8"));
					param.put("bank_name", URLEncoder.encode(param.get("bank_name"), "utf-8"));
					String body = ToolUtil.getToolUtil().formatUrlMap(param, null, false, false, true);
					String response = HttpRequest.get(url).body(body).execute().body();
					logger.info("代付结果响应:" + response);
					JSONObject respObj = JSONObject.parseObject(response);
					if (respObj.getBooleanValue("state")) {
						i++;
					}
				}
			if (i == list.size()) {
				return ReturnMsgEnum.success.setMsg("商户代付受理成功").toJson();
			} else if (i == 0) {
				return ReturnMsgEnum.error.setMsg("商户代付受理失败,请查看代付记录").toJson();
			} else {
				return ReturnMsgEnum.error.setMsg("部分商户代付受理失败,请查看代付记录").toJson();
			}
		} else {
			return ReturnMsgEnum.error.setMsg("请选择代付记录数").toJson();
		}
	}

	@PostMapping("/valid/code")
	public JSONObject validCode(@RequestParam(value = "code") String code) {
		// 验证码校验
		SMS.verify(code);
		if (!ReturnMsgEnum.isSucc)
			return ReturnMsgEnum.returnMsg();
		return ReturnMsgEnum.returnMsg();
	}

	@PreAuthorize("hasAuthority('daifu_pay')")
	@PostMapping("/parse/excel")
	public JSONObject parseExcel(@RequestParam("file") MultipartFile file) {
		try {
			List<MerchantPayeeVo> list = new ArrayList<MerchantPayeeVo>();
			InputStream inputStream = file.getInputStream();
			ExcelReader reader = ExcelUtil.getReader(inputStream);
			List<List<Object>> lists = reader.read(1);
			for (int i = 0; i < lists.size(); i++) {
				MerchantPayeeVo merPayeeVo = new MerchantPayeeVo();
				merPayeeVo.setMerchantId(String.valueOf(lists.get(i).get(0)));
				merPayeeVo.setBankName(String.valueOf(lists.get(i).get(1)));
				merPayeeVo.setRealName(String.valueOf(lists.get(i).get(2)));
				merPayeeVo.setBankCode(String.valueOf(lists.get(i).get(3)));
				merPayeeVo.setBankNumber(String.valueOf(lists.get(i).get(4)));
				merPayeeVo.setTotalAmount(String.valueOf(lists.get(i).get(5)));
				list.add(merPayeeVo);
			}
			return this.batchPayee(list);
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			logger.info("批量代付异常{}", e.getMessage());
			return ReturnMsgEnum.error.setMsg("模板解析错误").toJson();
		}
	}

	/**
	 * 批量代付
	 * 
	 * @param list
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public JSONObject batchPayee(List<MerchantPayeeVo> list) throws UnsupportedEncodingException {
		// String url = "http://localhost:8083/payee";
		String url = "http://api.qyiol.com/payee";
		int i = 0;
		String merId = null;
		if (!list.isEmpty())
			merId = list.get(0).getMerchantId();
		Optional<Merchant> optional = merchantService.findByAccount(merId);
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("代付商户不存在").toJson();
		JSONObject result = list.get(0).batchValid(optional.get(), list);
		if (result.getBooleanValue("state")) {
			for (MerchantPayeeVo vo : list) {
				Map<String, String> param = new HashMap<String, String>();
				String orderNumber = "DF".concat(DateUtil.getDate("yyMMddHHmmssSSS")).concat(CommonUtil.getRandom(6));
				param.put("merchant_id", vo.getMerchantId());
				param.put("merchant_order", orderNumber);
				param.put("total_amount", vo.getTotalAmount());
				param.put("bank_code", vo.getBankCode());
				param.put("real_name", vo.getRealName());
				param.put("bank_name", vo.getBankName());
				param.put("bank_number", vo.getBankNumber());
				String signStr = ToolUtil.getToolUtil().formatUrlMap(param, null, true, false, true);
				String sign = MD5Util.sign(signStr, optional.get().getMerchantKey(), "utf-8");
				param.put("sign", sign);
				param.put("real_name", URLEncoder.encode(param.get("real_name"), "utf-8"));
				param.put("bank_name", URLEncoder.encode(param.get("bank_name"), "utf-8"));
				String body = ToolUtil.getToolUtil().formatUrlMap(param, null, false, false, true);
				String response = HttpRequest.get(url).body(body).execute().body();
				logger.info("代付结果响应:" + response);
				JSONObject respObj = JSONObject.parseObject(response);
				if (respObj.getBooleanValue("state")) {
					i++;
				}
			}
			if (i == list.size()) {
				return ReturnMsgEnum.success.setMsg("商户代付受理成功").toJson();
			} else if (i == 0) {
				return ReturnMsgEnum.error.setMsg("商户代付受理失败,请查看代付记录").toJson();
			} else {
				return ReturnMsgEnum.error.setMsg("部分商户代付受理失败,请查看代付记录").toJson();
			}
		}
		return result;
	}
}
