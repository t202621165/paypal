package com.iwanol.paypal.until;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.security.SecurityManager;

/**
 * 腾讯云短信单发模版发送器
 * @author International
 * 2018年7月3日 上午10:49:32
 */
public class SMS implements Serializable{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;

	/**腾讯云短信单发模版APP_ID*/
	public final static Integer APP_ID = 1400026790;
	
	/**腾讯云短信单发模版密钥*/
	public final static String APP_KEY = "413ef0456665971b62fb82479849715d";
	
	/**腾讯云短信单发模版ID*/
	public final static Integer TEMP_MARK_ID = 13597;
	
	/**验证码有效时间(单位: 分)*/
	private final static Integer TIME = 5;  //验证码有效时间：5分钟
	
	/**验证码获取间隔时间(单位: 分)*/
	private final static Integer INTERVAL_TIME = 1;  //验证码再次获取间隔时间：1分钟
	
	/**短信验证码：六位数随机数*/
	private String sms_code; // 验证码
	
	/**获取短信验证码的手机号码*/
	private String phone_number; // 获取验证码的手机号码
	
	/**验证码到期时间*/
	private Date expiration_time; // 验证码到期时间
	
	/**验证码发送间隔时间*/
	private Date activation_date; // 再次获取验证码的时间：两分钟之后可再次获取
	
	/**时效验证*/
	private boolean is_effective; // true：验证码有效期内无需再次获取验证码
	
	/**腾讯云短信发送器发送参数*/
	private String[] params = new String[2];
	
	/**从session中的获取的验证码*/
	private static SMS sessionCode;
	
	public SMS() {
		
	}
	
	public void init(String phoneNumber) {
		this.phone_number = phoneNumber;
		this.sms_code = CommonUtil.getRandom(6);
		this.params[0] = this.sms_code;
		this.params[1] = TIME.toString();
		this.expiration_time = DateUtil.getBeforeOrAfterDateByDate(Calendar.MINUTE, TIME);
		this.activation_date = DateUtil.getBeforeOrAfterDateByDate(Calendar.MINUTE, INTERVAL_TIME);
	}
	
	/**
	 * 短信发送验证
	 * @return
	 */
	public boolean sendValidate() {
		// 验证码是否已发送： session中存在验证码
		if (isSend()) {
			// 验证码有效期验证
			if (isValid()) {
				// 是否开启时效验证
				if (is_effective) {
					ReturnMsgEnum.success.setMsg("IS_EFFECTIVE");
					return false;
				}else{// 未开启
					// 验证码发送功能是否可用
					if (!isUsable()) {
						ReturnMsgEnum.error.setMsg("请 " + INTERVAL_TIME +" 分钟之后重新获取！");
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 是否已发送验证码
	 * @return
	 */
	public static boolean isSend() {
		// 是否已获取验证码
		Object obj = SecurityManager.getValueFromSession("SMS");
		if (obj == null)
			return false;
		sessionCode = (SMS) obj;
		return true;
	}
	
	/**
	 * 短信验证码发送器
	 * @return
	 */
	public ReturnMsgEnum sender() {
		try {
			// 用户手机号码验证
			if (!VerifyUtil.isPhoneNumber(phone_number))
				return ReturnMsgEnum.error.setMsg("手机号码有误！");
			
			// 初始化腾讯云短信模版单发器
			SmsSingleSender sender = new SmsSingleSender(APP_ID, APP_KEY);
			
			// 向指定手机用户发送短信，同步返回处理结果
			SmsSingleSenderResult result = sender.sendWithParam("86", phone_number, TEMP_MARK_ID, params, "", "", "");
			if (result.result != 0)
				return ReturnMsgEnum.error.setMsg("验证码发送失败！"+result.errMsg);
			
			// 将验证码和到期时间存入session
			logger.info("【验证码:{}】",this.getSms_code());
			SecurityManager.setValue2Session("SMS", this);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnMsgEnum.error.setMsg("系统异常！"+e.getMessage());
		}
		return ReturnMsgEnum.success.setMsg("我们已将验证码发送至您尾号为："+phone_number.substring(7)+"的手机，请注意查收！");
	}
	
	/**
	 * 验证码获取功能是否可用
	 * @return
	 */
	public boolean isUsable() {
		if (sessionCode.activation_date.compareTo(new Date()) < 0)
			return true;
		return false;
	}
	
	/**
	 * 验证码有效期判断
	 */
	public static boolean isValid() {
		if (sessionCode.expiration_time.compareTo(new Date()) < 0)
			return false;
		return true;
	}
	
	/**
	 * 验证码校验
	 * @param code
	 * @return
	 */
	public static ReturnMsgEnum verify(String code) {
		// 是否已获取验证码
		if (!isSend())
			return ReturnMsgEnum.error.setMsg("请先获取验证码！");
		
		// 验证码有效期验证
		if (!isValid())
			return ReturnMsgEnum.error.setMsg("验证码已过期请重新获取！");
		
		// 验证码真实性验证
		if (!code.equals(sessionCode.sms_code))
			return ReturnMsgEnum.error.setMsg("您输入的验证码有误，请重新输入！");
		
		// 未开启时效验证则清除session
		if (!sessionCode.is_effective)
			SecurityManager.removeSession("SMS");
		return ReturnMsgEnum.success.setMsg("验证通过！");
	}
	
	public String getSms_code() {
		return sms_code;
	}

	public void setSms_code(String sms_code) {
		this.sms_code = sms_code;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public boolean isIs_effective() {
		return is_effective;
	}

	public void setIs_effective(boolean is_effective) {
		this.is_effective = is_effective;
	}

	public Date getExpiration_time() {
		return expiration_time;
	}

	public void setExpiration_time(Date expiration_time) {
		this.expiration_time = expiration_time;
	}

	public Date getActivation_date() {
		return activation_date;
	}

	public void setActivation_date(Date activation_date) {
		this.activation_date = activation_date;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "SMS [sms_code=" + sms_code + ", phone_number=" + phone_number + ", expiration_time=" + expiration_time
				+ ", activation_date=" + activation_date + ", is_effective=" + is_effective + ", params="
				+ Arrays.toString(params) + "]";
	}

}
