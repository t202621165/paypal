package com.iwanol.paypal.base.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.iwanol.paypal.domain.Product;

/**
 * 银行枚举类
 * @author International
 * 2018年7月07日 下午5:20:20
 */
public enum ProductsEnum {
	
	/**支付宝*/alipay(0, "支付宝", "支付宝（中国）网络技术有限公司是国内领先的第三方支付平台，致力于提供“简单、安全、快速”的支付解决方案。", null, 1),
	
	/**微信*/wechat(0, "微信", "微信支付是集成在微信客户端的支付功能，用户可以通过手机完成快速的支付流程。", null, 2),
	
	/**QQ钱包*/qpay(0, "QQ钱包", "QQ钱包是基于腾讯QQ的移动支付品牌，QQ钱包提供APP支付、扫码支付、NFC支付等支付方式。", null, 3),
	
	/**蚂蚁花呗*/hbpay(0, "蚂蚁花呗", "蚂蚁花呗是蚂蚁金服推出的一款消费信贷产品。用户在消费时，可以预支蚂蚁花呗的额度，享受“先消费，后付款”的购物体验。", null, 4),
	
	/**财付通*/tenpay(0, "财付通", "财付通（Tenpay）是由腾讯公司推出的专业在线支付平台，其核心业务是帮助在互联网上进行交易的双方完成支付和收款。致力于为互联网用户和企业提供安全、便捷、专业的在线支付服务。财付通支持全国各大银行的网银支付，用户也可以先充值到财付通，享受更加便捷的财付通余额支付体验。", null, 5),
	
	/**银联扫码*/ecode(0, "银联扫码", "银联二维码支付产品是成员机构APP的跨行转接交换产品，通过二维码条码交互方式，实现资金收付和增值应用，主要提供消费、转账及提现等方案，包括用户主扫和用户被扫两种模式。除了中国银行卡联合组织所有成员，银联二维码支付还支持京东、美团、大众点评等第三方支付APP。", null, 6),
	
	/**支付宝H5*/h5_alipay(0, "支付宝H5", "支付宝手机H5支付", null, 7),
	
	/**微信H5*/h5_wechat(0, "微信H5", "微信手机H5支付", null, 8),
	
	/**网银支付*/ebank(0, "网银支付", "中国银联是中国银行卡联合组织，通过银联跨行交易清算系统，实现商业银行系统间的互联互通和资源共享，保证银行卡跨行、跨地区和跨境的使用。", null, 9),
	
	/**中国银行*/BOC(1, "中国银行", "", ebank, 10),/**中国农业银行*/ABC(1, "农业银行", "", ebank, 11),
	
	/**中国建设银行*/CCB(1, "建设银行", "", ebank, 12),/**中国工商银行*/ICBC(1, "工商银行", "", ebank, 13),/**招商银行*/CMB(1, "招商银行", "", ebank, 14),
	
	/**交通银行*/COMM(1, "交通银行", "", ebank, 15),/**中国民生银行*/CMBC(1, "民生银行", "", ebank, 16),/**中国邮政储蓄银行*/PSBC(1, "邮政储蓄", "", ebank, 17),
	
	/**中信银行*/CITIC(1, "中信银行", "", ebank, 19),/**广大银行*/CEB(1, "光大银行", "", ebank, 19),/**兴业银行*/CIB(1, "兴业银行", "", ebank, 20),
	
	/**平安银行*/PINGAN(1, "平安银行", "", ebank, 21),/**广州银行*/GZCB(1, "广州银行", "", ebank, 22),/**广州发展银行*/GDB(1, "广发银行", "", ebank, 23),
	
	/**北京银行*/BOB(1, "北京银行", "", ebank, 24),/**上海银行*/BOSC(1, "上海银行", "", ebank, 25),/**上海浦东发展银行*/SPDB(1, "浦发银行", "", ebank, 26);
	
	/**银行名称*/
	private String name;
	
	private String desc;
	
	private Integer type;
	
	private Integer sort;
	
	private ProductsEnum parent;
		
	private ProductsEnum(Integer type, String name, String desc, ProductsEnum parent, Integer sort) {
		this.type = type;
		this.name = name;
		this.desc = desc;
		this.sort = sort;
		this.parent = parent;
	}
	
	/**
	 * 获取所有产品对象和产品对应子对象
	 * @param pEnum
	 * @return
	 */
	public static List<Product> getProductWithChild(ProductsEnum pEnum, Product parent) {
		List<ProductsEnum> enums = getEnumList();
		return enums.stream().filter(e -> e.parent == pEnum).map(e -> {
			Product product = e.getProductByEnum();
			product.setParent(pEnum == null?null:pEnum.getProductByEnum());
			product.setParent(parent);
			List<Product> list = getProductWithChild(e, product);
			product.setChildren(list);
			return product;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 获取产品枚举数组
	 * @return
	 */
	public static List<ProductsEnum> getEnumList() {
		return new ArrayList<ProductsEnum>(Arrays.asList(ProductsEnum.values()));
	}
	
	/**
	 * 将枚举类转换成product对象
	 * @return
	 */
	public Product getProductByEnum() {
		return Product.getProductByEnum(this);
	}
	
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public Integer getType() {
		return type;
	}

	public ProductsEnum getParent() {
		return parent;
	}

	public void setParent(ProductsEnum parent) {
		this.parent = parent;
	}

	public Integer getSort() {
		return sort;
	}

}
