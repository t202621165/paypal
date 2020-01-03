package com.iwanol.paypal.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.BanksEnum;
import com.iwanol.paypal.base.enums.ResourcesEnum;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.Role;
import com.iwanol.paypal.domain.SystemSet;
import com.iwanol.paypal.exception.ErrorCode;
import com.iwanol.paypal.exception.MyException;
import com.iwanol.paypal.security.SecurityManager;
import com.iwanol.paypal.service.BankService;
import com.iwanol.paypal.service.GalleryService;
import com.iwanol.paypal.service.MerchantService;
import com.iwanol.paypal.service.NoticeService;
import com.iwanol.paypal.service.OrderService;
import com.iwanol.paypal.service.PayeeService;
import com.iwanol.paypal.service.ProductService;
import com.iwanol.paypal.service.ResourceService;
import com.iwanol.paypal.service.RoleService;
import com.iwanol.paypal.service.SystemSetService;
import com.iwanol.paypal.service.UserService;
import com.iwanol.paypal.until.MD5Util;
import com.iwanol.paypal.until.SMS;
import com.iwanol.paypal.vo.OrderVo;

@Controller
public class WebController {
	
	@Autowired
	private BankService bankService;
	@Autowired
	private ProductService productService;
	@Autowired
	private GalleryService galleryService;
	@Autowired
	private MerchantService merchantService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PayeeService payeeService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private SystemSetService systemSetService;
	
	@GetMapping("/")
	public String root(){
		return "redirect:/index.html";
	}
	
	@GetMapping("/exception")
	public String testException() throws MyException{
		 throw new MyException(ErrorCode.ERROR,"发送错误!");
	}
	
	@ModelAttribute
	public void getEntity(Model model) {
		model.addAttribute("system", systemSetService.findWebCopyRight());
	}
	
	/**
	 * 控制台首页
	 * @return
	 */
	@PreAuthorize("hasAuthority('index.html')")
	@GetMapping("/index.html")
	public String indexUI(Model model){
		model.addAttribute("data", orderService.findOrderDayReport());
		model.addAttribute("proReport", orderService.findOrderProductDayReport());
		model.addAttribute("waitCount", new OrderVo(2).findTotalCountByQl());
		model.addAttribute("merchantVo", merchantService.findMerchantCount());
		return "index/index";
	}
	
	/**
	 * 资金列表
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('funds.html')")
	@GetMapping("/funds.html")
	public String funds(Model model){
		return "funds/funds";
	}
	
	/**
	 * T+0结算页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('funds_t0')")
	@GetMapping("/funds_t0")
	public String fundsT0(Model model){
		return "funds/t0";
	}
	
	/**
	 * T+1结算页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('funds_t1')")
	@GetMapping("/funds_t1")
	public String fundsT1(Model model){
		return "funds/t1";
	}
	
	/**
	 * 提现页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('funds_pay')")
	@GetMapping("/funds_pay")
	public String pay(Model model){
		return "funds/pay";
	}
	
	/**
	 * 代付页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('daifu_pay')")
	@GetMapping("/daifu_pay")
	public String daifuPay(Model model){
		model.addAttribute("allBanks",BanksEnum.toJSONArray());
		return "funds/daifu";
	}
	
	/**
	 * 充扣页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('funds.html')")
	@GetMapping("/cong_kou")
	public String congkou(){
		return "funds/cong_kou";
	} 
	
	
	/**
	 * 批付记录列表
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('reply.html')")
	@GetMapping("reply.html")
	public String reply(Model model){
		model.addAttribute("users",userService.findEntitys());
		return "funds/reply";
	}
	
	/**
	 * 批付记录详情
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('reply_details.html')")
	@GetMapping("reply_details.html")
	public String replyDetails(Model model,@RequestParam(value="id", required=false) Long id){
		model.addAttribute("replyId",id);
		return "funds/reply_details";
	}
	
	/**
	 * 资金自提页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('funds_self')")
	@GetMapping("/funds_self")
	public String self(Model model){
		SystemSet system = systemSetService.findByMark();
		model.addAttribute("system", system);
		if (system != null) {
			Merchant merchant = system.getMerchant();
			if (merchant != null && merchant.getBankBindState()) {
				model.addAttribute("banks", bankService.findByMerchantAndPayeeState(merchant, true));
				model.addAttribute("allBanks",BanksEnum.toJSONArray());
				model.addAttribute("payees", payeeService.findEntitys());
			}
		}
		return "funds/self";
	}
	
	/**
	 * 商户管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('merchant.html')")
	@GetMapping("/merchant.html")
	public String merchant(Model model){
		model.addAttribute("products", productService.findEntitys());
		return "merchant/merchant";
	}
	
	/**
	 * 商户管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('business.html')")
	@GetMapping("/business.html")
	public String business(Model model){
		return "merchant/business";
	}
	
	/**
	 * 商户详情页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('profile.html')")
	@GetMapping("/profile.html")
	public String profile(Model model, Merchant merchant){
		merchantService.findMerchantDetails(merchant, model);
		model.addAttribute("BanksEnum", BanksEnum.toJSONArray());
		return "merchant/profile";
	}
	
	/**
	 * 进去商户管理后台
	 * @param request
	 * @param merchant
	 * @return
	 */
	@PreAuthorize("hasAuthority('into_merchant.html')")
	@GetMapping(value = "/into_merchant.html")
	public @ResponseBody JSONObject intoMerchant(Merchant vo){
		JSONObject json = new JSONObject();
		Merchant merchant = merchantService.findEntity(vo.getId());
		SystemSet system = systemSetService.findByMark();
		if (!StringUtils.isEmpty(merchant) && !StringUtils.isEmpty(system)){
			String account = merchant.getAccount();
			String signStr = String.format("%s@@iwanol@@%s", account, merchant.getPassWord());
			String sign = MD5Util.sign(signStr, "", "UTF-8");
			String domain = system.getDomainName();
			json.put("domain", domain+"/come.html");
			json.put("account", account);
			json.put("sign", sign);
		}
		return json;
	}
	
	/**
	 * 订单管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('order.html')")
	@GetMapping("/order.html")
	public String order(Model model, HttpServletRequest request){
		model.addAttribute("orderNumber",request.getQueryString());
		model.addAttribute("products",productService.findAllByType(0));
		model.addAttribute("gallerys",galleryService.findEntitys());
		return "order/order";
	}
	
	/**
	 * 产品管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('product.html')")
	@GetMapping("/product.html")
	public String product(Model model){
		model.addAttribute("products", productService.findByParentOrderBySort(null));
		return "paypal/product";
	}
	
	/**
	 * 网银产品页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('product.html')")
	@GetMapping("/ebank.html")
	public String ebank(Model model,Product product){
		model.addAttribute("product", productService.findProductAndGallerysById(product.getId()));
		model.addAttribute("children", productService.findByParentOrderBySort(product));
		return "paypal/ebank";
	}

	/**
	 * 通道管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('gallery.html')")
	@GetMapping("/gallery.html")
	public String gallery(Model model){
		return "paypal/gallery";
	}
	
	/**
	 * 通道详情页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('g_details.html')")
	@GetMapping("/g_details.html")
	public String galleryEdit(Model model, Gallery gallery){
		galleryService.findGalleryDetails(gallery, model);
		return "paypal/g_details";
	}
	
	/**
	 * 用户管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('user.html')")
	@GetMapping("/user.html")
	public String user(Model model){
		model.addAttribute("users",userService.findEntitys());
		model.addAttribute("roles",roleService.findEntitys());
		return "system/user";
	}
	
	/**
	 * 角色管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('role.html')")
	@GetMapping("/role.html")
	public String role(Model model){
		return "system/role";
	}
	
	/**
	 * 角色详情页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('r_details.html')")
	@GetMapping("/r_details.html")
	public String roleDetails(Model model, Role role){
		model.addAttribute("role",roleService.findOneById(role.getId()));
		model.addAttribute("users",userService.findEntitys());
		model.addAttribute("resources",resourceService.findResourcesByParent());
		return "system/role_details";
	}
	
	/**
	 * 资源管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('resource.html')")
	@GetMapping("/resource.html")
	public String resource(Model model){
		model.addAttribute("hasAllResource", ResourcesEnum.hasAllResource(resourceService.count()));
		model.addAttribute("resources", resourceService.findResourcesByParent());
		return "system/resource";
	}
	
	/**
	 * 系统设置页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('system.html')")
	@GetMapping("/system.html")
	public String system(Model model){
		SystemSet system = systemSetService.findByMark();
		model.addAttribute("system", system);
		model.addAttribute("merchant", system.getMerchant());
		return "system/system";
	}
	
	/**
	 * 消息管理页面
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasAuthority('notice.html')")
	@GetMapping("/notice.html")
	public String notice(Model model){
		model.addAttribute("merchants", noticeService.findAllMerchant());
		return "notice/notice";
	}
	
	/**
	 * 后台登录页面
	 * @return
	 */
	@GetMapping("/login")
	public String login(Model model){
		if (!SecurityManager.isAnonymous())
			return "redirect:index.html";
		return "login/login";
	}
	
	@GetMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
		return "forward:login";
	}
	
	/**
	 * 短信验证码获取
	 * @return 
	 */
	@PreAuthorize("hasAuthority('payee/pay')")
	@PostMapping("sms")
	public @ResponseBody JSONObject getSMSCode(SMS sms) {
		return systemSetService.getSMSCode(sms).toJson();
	}
	
	@GetMapping("/403")
	public String error403(Model model) {
		return "error/403";
	}
	
	@GetMapping("/404")
	public String error404(){
		return "error/404";
	}
	
	@GetMapping("/405")
	public String error405(Model model) {
		return "redirect:404";
	}
	
	@GetMapping("/500")
	public String error500(Model model) {
		return "error/500";
	}
	
}
