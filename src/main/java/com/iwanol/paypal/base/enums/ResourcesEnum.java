package com.iwanol.paypal.base.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.iwanol.paypal.domain.Resource;

public enum ResourcesEnum {

	index_html(null,"fa-home","index.html","首　　页",null,1),
	noticeManage(null,"fa-commenting","noticeManage","消息管理",null,2),
	merchantManage(null,"fa-male","merchantManage","商户管理",null,3),
	orderManage(null,"fa-reorder","orderManage","订单管理",null,4),
	fundsManage(null,"fa-rmb","fundsManage","资金管理",null,5),
	paypalManage(null,"fa-paypal","paypalManage","支付网关",null,6),
	setupManage(null,"fa-cogs","setupManage","系统管理",null,7),
	notice_html(null,"fa-comments-o","notice.html","消息列表",noticeManage,8),
	merchant_html(null,"fa-id-card","merchant.html","商户列表",merchantManage,9),
	business_html(null,"fa-legal","business.html","偏重业务",merchantManage,10),
	order_html(null,"fa-newspaper-o","order.html","订单列表",orderManage,11),
	funds_html(null,"fa-dollar","funds.html","资金列表",fundsManage,12),
	funds_t1(null,"fa-tumblr","funds_t1","T+1结算",fundsManage,13),
	funds_t0(null,"fa-tumblr","funds_t0","T+0结算",fundsManage,14),
	funds_pay(null,"fa-rouble","funds_pay","提现业务",fundsManage,15),
	daifu_pay(null,"fa-rouble","daifu_pay","商户代付",fundsManage,16),
	cong_kou(null,"fa-rouble","cong_kou","商户充扣业务",fundsManage,18),
	funds_self(null,"fa-ils","funds_self","资金自提",fundsManage,16),
	reply_html(null,"fa-sitemap","reply.html","批付记录",fundsManage,17),
	product_html(null,"fa-pinterest-p","product.html","产品管理",paypalManage,18),
	gallery_html(null,"fa-glide-g","gallery.html","通道管理",paypalManage,19),
	user_html(null,"fa-user-o","user.html","用户管理",setupManage,20),
	role_html(null,"fa-users","role.html","角色管理",setupManage,21),
	resource_html(null,"fa-envira","resource.html","权限管理",setupManage,22),
	system_html(null,"fa-gear","system.html","系统设置",setupManage,23),
	notice_add(null,"fa-plus","notice/add","添加消息",notice_html,24),
	notice_edit(null,"fa-edit","notice/state","启用/禁用",notice_html,25),
	notice_delete(null,"fa-trash","notice/delete","删除消息",notice_html,36),
	merchant_gallerys(null,"fa-exchange","merchant/gallerys","切换通道",merchant_html,27),
	profile_html(null,"fa-eye","profile.html","商户详情",merchant_html,28),
	into_merchant_html(null,"fa-sign-in","into_merchant.html","商户后台",merchant_html,29),
	merchantDelete(null,"fa-trash","merchant/delete","删除商户",merchant_html,30),
	businessAdd(null,"fa-plus","business/add","添加业务",business_html,31),
	businessEdit(null,"fa-edit","business/edit","编辑业务",business_html,32),
	businessDelete(null,"fa-trash","business/delete","删除业务",business_html,33),
	orderDetails(null,"fa-eye","order/details","订单详情",order_html,34),
	orderSend(null,"fa-send-o","order/send","订单补发",order_html,35),
	orderRefund(null,"fa-sign-out","order/refund","订单退款",order_html,36),
	fundsT1Settlement(null,"fa-sitemap","funds/t1_settlement","结算",funds_t1,37),
	fundsT0Settlement(null,"fa-sitemap","funds/t0_settlement","结算",funds_t0,38),
	fundsCheck(null,"fa-check-square-o","funds/check","同意审核",funds_pay,39),
	fundsCheck_refuse(null,"fa-ban","funds/check_refuse","拒绝审核",funds_pay,40),
	fundsPay(null,"fa-credit-card-alt","funds/pay","同意付款",funds_pay,41),
	fundsPayRefuse(null,"fa-ban","funds/pay_refuse","拒绝付款",funds_pay,42),
	reply_details_html(null,"fa-eye","reply_details.html","批付详情",reply_html,43),
	fundsReplyPay(null,"fa-credit-card-alt","funds/reply_pay","付款",reply_html,44),
	fundsExport(null,"fa-share-square-o","funds/reply_export","导出",reply_html,45),
	fundsReplyDelete(null,"fa-trash","funds/reply_delete","删除",reply_html,46),
	productAdd(null,"fa-plus","product/add","添加",product_html,47),
	productDelete(null,"fa-trash","product/delete","删除",product_html,48),
	productEdit(null,"fa-edit","product/edit","编辑",product_html,49),
	galleryAdd(null,"fa-plus","gallery/add","添加",gallery_html,50),
	galleryDelete(null,"fa-trash","gallery/delete","删除",gallery_html,51),
	galleryEdit(null,"fa-edit","gallery/edit","编辑",gallery_html,52),
	g_details_html(null,"fa-eye","g_details.html","详情",gallery_html,53),
	userAdd(null,"fa-plus","user/add","添加",user_html,54),
	userDelete(null,"fa-edit","user/delete","删除",user_html,55),
	userPass(null,"fa-key","user/pass","修改密码",user_html,56),
	userRoles(null,"fa-users","user/roles","添加角色",user_html,57),
	roleAdd(null,"fa-plus","role/add","添加",role_html,58),
	roleDelete(null,"fa-trash","role/delete","删除",role_html,59),
	roleEdit(null,"fa-edit","role/edit","编辑",role_html,60),
	r_details_html(null,"fa-eye","r_details.html","详情",role_html,61),
	systemEdit(null,"fa-edit","system/edit","系统编辑",system_html,62),
	payeePay(null,"fa-cny","payee/pay","资金代付",system_html,63),
	payeeAdd(null,"fa-plus","payee/add","代付账户添加",system_html,64),
	payeeDelete(null,"fa-trash","payee/delete","代付账户删除",system_html,65),
	payeeDdit(null,"fa-edit","payee/edit","代付账户编辑",system_html,66);
	
	private Long id;
	
	private String icon;
	
	private String mark;
	
	private String name;
	
	private ResourcesEnum parent;
	
	private Integer sort;
	
	/**
	 * 获取所有资源对象和资源对应子对象
	 * @param rEnum
	 * @return
	 */
	public static List<Resource> getResourceWhithChild(ResourcesEnum rEnum, Resource parent) {
		List<ResourcesEnum> enums = getEnumList();
		return enums.stream().filter(e -> e.parent == rEnum).map(e -> {
			Resource resource = e.getResourceByEnum();
			resource.setParent(parent);
			List<Resource> list = getResourceWhithChild(e, resource);
			resource.setChildern(new HashSet<Resource>(list));
			return resource;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 获取资源枚举数组
	 * @return
	 */
	public static List<ResourcesEnum> getEnumList() {
		return new ArrayList<ResourcesEnum>(Arrays.asList(ResourcesEnum.values()));
	}
	
	public static boolean hasAllResource(Long count) {
		return count >= ResourcesEnum.values().length;
	}
	
	/**
	 * 将枚举类转换成resource对象
	 * @return
	 */
	public Resource getResourceByEnum() {
		return Resource.getResourceByEnum(this);
	}
	
	public static List<Resource> getResource(List<Resource> resources) {
		List<ResourcesEnum> enums = getEnumList();
		
		return enums.stream().map(rEnum -> {
			String mark = rEnum.mark;
			resources.stream().filter(r -> mark.equals(r.getResourceMark())).forEach(r -> {
				rEnum.setId(r.getId());
			});
			
			Resource resource = rEnum.getResourceByEnum();
			if (rEnum.parent != null)
				resource.setParent(rEnum.parent.getResourceByEnum());
			
			return resource;
		}).collect(Collectors.toList());
	}
	
	private ResourcesEnum(Long id, String icon, String mark, String name, ResourcesEnum parent, Integer sort) {
		this.id = id;
		this.icon = icon;
		this.mark = mark;
		this.name = name;
		this.parent = parent;
		this.sort = sort;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getName() {
		return name;
	}

	public ResourcesEnum getParent() {
		return parent;
	}

	public Integer getSort() {
		return sort;
	}

}
