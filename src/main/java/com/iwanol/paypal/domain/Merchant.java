package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 平台商户实体
 * @author leo
 *
 */
@Entity
public class Merchant implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 主键id
	
	@Column(length=14,nullable=false,unique=true)
	private String account; //商户账号   唯一  
	
	@Column(length=64,nullable=false)
	private String passWord; //商户登陆密码
	
	@Temporal(TemporalType.DATE)
	private Date regDate; //商户注册时间
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastDate; //商户最后登陆时间
	
	@Column(length=10,unique=true)
	@NotNull(message="昵称不能为空!")
	private String nickName; //商户昵称
	
	@Column(length=30)
	private String iconUrl; //商户头像
	
	@Column(length=32,nullable=false)
	private String merchantKey; //商户密钥
	
	@Column(length=1)
	private Integer state; //商户状态 0 未激活 1 已激活 2系统禁用 3.关闭支付下单功能
	
	@Email(message="邮箱格式不正确!")
	@Column(length=30, nullable = false, unique = true)
	@NotNull(message="邮箱不能为空!")
	private String email; //商户邮箱

	@Column(nullable=false)
	private Boolean certificationState; //商户认证状态 false/0: 未认证  true/1: 已认证

	@Column(nullable=false) // false/0: 未绑定  true/1: 已绑定
	private Boolean bankBindState; //银行卡绑定状态 
	
	@Column(length=11)
	private String telPhone; //商户手机号码
	
	@Column(name="service_qq",length=11)
	private String serviceQQ; // 客服QQ
	
	@Column(length=11)
	private String servicePhone; // 客服电话
	
	@Column(length=50)
	private String company; //商户公司名称

	@Column(nullable=false)
	private Boolean type; // false/0: 个人商户  true/1: 企业商户
	
	private Integer settlementType; //商户结算类型 0 为 T0 1为T1;

	@Column(nullable=false)
	private Boolean agency;// true/1 为代理  false/0: 非代理 
	
	private String retUrl; //同步返回地址
	
	private String notifyUrl; //异步通知地址
	
	@Column(length=25)
	private String organizationNumber;//组织机构代码
	
	private BigDecimal fee; //单笔提现手续费 默认5.00元
	
	private Boolean isOpenTail = Boolean.FALSE; //是否开启尾额
	
	private Integer tailRatio = 0; //尾额结算比例
	
	@Column(nullable = false)
	private BigDecimal minAmount;
	
	@Column(nullable = false)
	private BigDecimal maxAmount;
	
	@JsonIgnore
	@OneToMany(mappedBy="merchant")
	private Set<PlatformOrder> platformOrders = new HashSet<PlatformOrder>(); //一个商户对应多个订单
	
	@JsonIgnore
	@OneToMany(mappedBy="merchant")
	private Set<ProductRate> productRates = new HashSet<ProductRate>(); //商户产品费率

	@JsonIgnore
	@OneToMany(mappedBy="merchant")
	private Set<MerchantProductGallery> merchantProductGallerys = new HashSet<MerchantProductGallery>();

	@JsonIgnore
	@OneToMany(mappedBy="merchant")
	private Set<Bank> banks = new HashSet<Bank>();//商户银行卡

	@JsonIgnore
	@OneToMany(mappedBy="merchant")
	private Set<SettleMent> settleMents = new HashSet<SettleMent>();

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	private MerchantBusiness merchantBusiness = new MerchantBusiness();
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	private Merchant merchant;
	
	@JsonIgnore
	@OneToMany(mappedBy="merchant")
	private List<Merchant> children = new ArrayList<Merchant>();
	
	@JsonIgnore
	@OneToMany(mappedBy="merchant",cascade=CascadeType.REMOVE)
	private Set<AccountDetails> accountDetalis = new HashSet<AccountDetails>();
	
	@Transient
	private Long count;
	
	public Merchant() {
		super();
	}
	
	public Merchant(Long id) {
		this.id = id;
	}
	
	public Merchant(Long id, String account, String email) {
		this.id = id;
		this.account = account;
		this.email = email;
	}
	
	/**
	 * 获取系统商户
	 * @param mark
	 * @param name
	 * @param email
	 * @return
	 */
	public static Merchant getSystemMerchant(String mark, String name, String email) {
		Merchant merchant = new Merchant();
		merchant.setAccount(mark);
		merchant.setNickName(mark);
		merchant.setCompany(name);
		merchant.setEmail(email);
		merchant.setMerchantBusiness(null);
		merchant.init();
		return merchant;
	}
	
	public Merchant(String account,String company,String email) {
		this.account = this.nickName = account;
		this.company = company;
		this.email = email;
		init();
	}
	
	public Merchant(Integer state,Long count) {
		this.state = state;
	}
	
	public void init() {
		if (passWord == null || "".equals(passWord))
			this.passWord = new BCryptPasswordEncoder().encode("000000");
		if (merchantKey == null || "".equals(merchantKey))
			this.merchantKey = UUID.randomUUID().toString().replaceAll("-", "");
		if (state == null)
			this.state = 1;
		if (fee == null)
			this.fee = new BigDecimal(5);
		if (settlementType == null)
			this.settlementType = 1;
		if (regDate == null)
			this.regDate = new Date();
		if (type == null)
			this.type = Boolean.FALSE;
		if (agency == null)
			this.agency = Boolean.FALSE;
		if (bankBindState == null)
			this.bankBindState = Boolean.FALSE;
		if (certificationState == null)
			this.certificationState = Boolean.FALSE;
		if (minAmount == null)
			this.minAmount = new BigDecimal(0);
		if (maxAmount == null)
			this.maxAmount = new BigDecimal(0);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getCertificationState() {
		return certificationState;
	}

	public void setCertificationState(Boolean certificationState) {
		this.certificationState = certificationState;
	}

	public Boolean getBankBindState() {
		return bankBindState;
	}

	public void setBankBindState(Boolean bankBindState) {
		this.bankBindState = bankBindState;
	}

	public String getTelPhone() {
		return telPhone;
	}

	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}

	public String getServiceQQ() {
		return serviceQQ;
	}

	public void setServiceQQ(String serviceQQ) {
		this.serviceQQ = serviceQQ;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public Integer getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(Integer settlementType) {
		this.settlementType = settlementType;
	}

	public Boolean getAgency() {
		return agency;
	}

	public void setAgency(Boolean agency) {
		this.agency = agency;
	}

	public String getRetUrl() {
		return retUrl;
	}

	public void setRetUrl(String retUrl) {
		this.retUrl = retUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(String organizationNumber) {
		this.organizationNumber = organizationNumber;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}
	

	public Boolean getIsOpenTail() {
		return isOpenTail;
	}

	public void setIsOpenTail(Boolean isOpenTail) {
		this.isOpenTail = isOpenTail;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Set<PlatformOrder> getPlatformOrders() {
		return platformOrders;
	}

	public void setPlatformOrders(Set<PlatformOrder> platformOrders) {
		this.platformOrders = platformOrders;
	}

	public Set<ProductRate> getProductRates() {
		return productRates;
	}

	public void setProductRates(Set<ProductRate> productRates) {
		this.productRates = productRates;
	}

	public Set<MerchantProductGallery> getMerchantProductGallerys() {
		return merchantProductGallerys;
	}

	public void setMerchantProductGallerys(Set<MerchantProductGallery> merchantProductGallerys) {
		this.merchantProductGallerys = merchantProductGallerys;
	}

	public Set<Bank> getBanks() {
		return banks;
	}

	public void setBanks(Set<Bank> banks) {
		this.banks = banks;
	}

	public Set<SettleMent> getSettleMents() {
		return settleMents;
	}

	public void setSettleMents(Set<SettleMent> settleMents) {
		this.settleMents = settleMents;
	}

	public MerchantBusiness getMerchantBusiness() {
		return merchantBusiness;
	}

	public void setMerchantBusiness(MerchantBusiness merchantBusiness) {
		this.merchantBusiness = merchantBusiness;
	}
	
	public void encPassword() {
		this.passWord = new BCryptPasswordEncoder().encode(this.passWord);
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public List<Merchant> getChildren() {
		return children;
	}

	public void setChildren(List<Merchant> children) {
		this.children = children;
	}
	
	public Set<AccountDetails> getAccountDetalis() {
		return accountDetalis;
	}

	public void setAccountDetalis(Set<AccountDetails> accountDetalis) {
		this.accountDetalis = accountDetalis;
	}
	
	public Integer getTailRatio() {
		return tailRatio;
	}

	public void setTailRatio(Integer tailRatio) {
		this.tailRatio = tailRatio;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public void checkState() {
		switch (state) {
		case 0:
			
			break;
		case 1:
			
			break;
		case 2:
			
			break;
		case 3:
			
			break;

		default:
			state = 0;
			break;
		}
	}

	@Override
	public String toString() {
		return "Merchant [id=" + id + ", account=" + account + ", passWord=" + passWord + ", regDate=" + regDate
				+ ", lastDate=" + lastDate + ", nickName=" + nickName + ", iconUrl=" + iconUrl + ", merchantKey="
				+ merchantKey + ", state=" + state + ", email=" + email + ", certificationState=" + certificationState
				+ ", bankBindState=" + bankBindState + ", telPhone=" + telPhone + ", serviceQQ=" + serviceQQ
				+ ", servicePhone=" + servicePhone + ", company=" + company + ", type=" + type + ", settlementType="
				+ settlementType + ", agency=" + agency + ", retUrl=" + retUrl + ", notifyUrl=" + notifyUrl
				+ ", organizationNumber=" + organizationNumber + ", fee=" + fee + ", minAmount=" + minAmount
				+ ", maxAmount=" + maxAmount + "]";
	}

}
