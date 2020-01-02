package com.iwanol.paypal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.base.vo.echarts.EchartsPie;
import com.iwanol.paypal.domain.Address;
import com.iwanol.paypal.domain.Certificate;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.MerchantBusiness;
import com.iwanol.paypal.domain.MerchantProductGallery;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.ProductRate;
import com.iwanol.paypal.repository.AccountDetailsRepository;
import com.iwanol.paypal.repository.AddressRepository;
import com.iwanol.paypal.repository.BankRepository;
import com.iwanol.paypal.repository.BusenessRepository;
import com.iwanol.paypal.repository.CertificateRepository;
import com.iwanol.paypal.repository.MerchantProductGalleryRepository;
import com.iwanol.paypal.repository.MerchantRepository;
import com.iwanol.paypal.repository.OrderRepository;
import com.iwanol.paypal.repository.ProductRateRepository;
import com.iwanol.paypal.repository.ProductRepository;
import com.iwanol.paypal.repository.SettleMentRepository;
import com.iwanol.paypal.vo.MerchantProductGalleryVo;
import com.iwanol.paypal.vo.MerchantVo;
import com.iwanol.paypal.vo.OrderVo;
import com.iwanol.paypal.vo.RateArray;

@Service
public class MerchantService extends BaseServiceImpl<Merchant,Merchant> {

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CertificateRepository certificateRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private SettleMentService settleMentService;
	@Autowired
	private SettleMentRepository settleMentRepository;
	@Autowired
	private BusenessRepository busenessRepository;
	@Autowired
	private ProductRateRepository productRateRepository;
	@Autowired
	private AccountDetailsRepository accountDetailsRepository;
	@Autowired
	private MerchantProductGalleryRepository merchantProductGalleryRepository;
	
	@Override
	public BaseRepository<Merchant, Long> getRepository() {
		return merchantRepository;
	}

	@SuppressWarnings("serial")
	@Override
	public Specification<Merchant> getSpecification(Merchant v) {
		return new Specification<Merchant>() {
			@Override
			public Predicate toPredicate(Root<Merchant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				// 根据ID查询
				Long id = v.getId();
				if (id != null) {
					list.add(cb.equal(root.get("id").as(Long.class),id));
				}
				// 根据注册类型查询
				Boolean type = v.getType();
				if (type != null) {
					list.add(cb.equal(root.get("type").as(Boolean.class),type));
				}
				// 根据代理状态查询
				Boolean agency = v.getAgency();
				if (agency != null) {
					list.add(cb.equal(root.get("agency").as(Boolean.class),agency));
				}
				// 根据商户状态查询
				Integer state = v.getState();
				if (state != null) {
					list.add(cb.equal(root.get("state").as(Integer.class),state));
				}
				// 根据结算类型查询
				Integer settlementType = v.getSettlementType();
				if (settlementType != null) {
					list.add(cb.equal(root.get("settlementType").as(Integer.class),settlementType));
				}
				Predicate[] predicates = new Predicate[list.size()];
				predicates = list.toArray(predicates);
				return cb.and(predicates);
			}
		};
	}
	
	public Optional<Merchant> findMerchantByAccount(String account) {
		return merchantRepository.findByAccount(account);
	}

	@Transactional
	public Certificate saveCertificate(Certificate certificate) {
		return certificateRepository.save(certificate);
	}
	
	@Transactional
	public Address saveAddress(Address address) {
		return addressRepository.save(address);
	}
	
	public MerchantBusiness saveBusiness(MerchantBusiness business) {
		return busenessRepository.save(business);
	}
	
	/**
	 * 商户日交易统计
	 * @param merchant
	 * @param model
	 */
	public void findMerchantDetails(Merchant merchant, Model model) {
		Optional<Merchant> optional = merchantRepository.findById(merchant.getId());
		if (optional.isPresent()) {
			Optional<Address> optional1 = addressRepository.findByMerchant(merchant);
			Optional<Certificate> optional2 = certificateRepository.findByMerchant(merchant);
			model.addAttribute("address", optional1.isPresent()?optional1.get():null);
			model.addAttribute("certificate", optional2.isPresent()?optional2.get():null);
			model.addAttribute("merchant", optional.get());
			model.addAttribute("rates", productRateRepository.findByMerchant(merchant));
			List<MerchantProductGalleryVo> voList = merchantProductGalleryRepository.findList(merchant);
			model.addAttribute("gallerys", new MerchantProductGalleryVo().listVo(voList));
			OrderVo vo = new OrderVo(merchant.getId());
			vo.setEntityManager(entityManager);
			vo.findOrderSumDateByState();
			model.addAttribute("orderSumDate", vo);
			vo.reSetGroupByBuf(new StringBuffer(" AND temp.state != 0 GROUP BY temp.product ORDER BY amount DESC"));
			model.addAttribute("data", new EchartsPie(vo.findOrderSumDate(),false));
		}
	}
	
	/**
	 * 商户删除
	 */
	@Override
	@Transactional
	public JSONObject deleteEntity(Long id) {
		Optional<Merchant> optional = merchantRepository.findById(id);
		if (optional.isPresent()) {
			Merchant merchant = optional.get();
			Long BankCount = bankRepository.findCountByMerchant(id);
			if (BankCount > 0)
				return ReturnMsgEnum.error.setMsg("当前商户还有未结算余额，不可删除！").toJson();
			Long count = orderService.findOrderCountBySpe(new OrderVo(id, null, null));
			if (count > 0) {
				ReturnMsgEnum.error.setMsg("当前商户近三个月内存在交易记录，不可删除！");
			}else{
				// 查询商户是否有未结算记录
				Long unsettledCount = settleMentService.findUnsettledCount(id, null);
				if (unsettledCount > 0)
					return ReturnMsgEnum.error.setMsg("当前商户有未结算记录，不可删除！").toJson();
				// 判断是否为代理
				if (merchant.getAgency())
					merchantRepository.updateAngencyMerchant(merchant);
				// 删除提现结算记录
				settleMentRepository.deleteByMerchant(merchant);
				// 删除商户认证信息
				certificateRepository.deleteByMerchant(merchant);
				// 删除商户地址
				addressRepository.deleteByMerchant(merchant);
				// 删除商户账户明细
				accountDetailsRepository.deleteByMerchant(merchant);
				// 删除商户订单
				orderRepository.deleteByMerchant(merchant);
				// 删除商户产品费率
				productRateRepository.deleteByMerchant(merchant);
				// 删除商户默认通道记录
				merchantProductGalleryRepository.deleteByMerchant(merchant);
				// 删除商户银行账户
				bankRepository.deleteByMerchant(merchant);
				// 删除当前商户
				merchantRepository.delete(merchant);
				ReturnMsgEnum.success.setMsg("商户“"+merchant.getNickName()+"”删除成功！");
			}
		}else{
			ReturnMsgEnum.error.setMsg("删除失败，当前商户不存在或已被删除！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	@Override
	public boolean addValidate(Merchant t) {
		return false;
	}

	/**
	 * 修改商户密码
	 * @param request
	 * @param merchant
	 * @return
	 */
	public JSONObject updatePassword(HttpServletRequest request, Merchant merchant) {
		String password = merchant.getPassWord();
		String confirmPass = request.getParameter("confirmPass");
		if (!password.equals(confirmPass)) {
			ReturnMsgEnum.error.setMsg("两次密码输入不一致！");
			return ReturnMsgEnum.returnMsg();
		}
		merchant.encPassword();
		merchantRepository.updatePassWordById(merchant.getId(), merchant.getPassWord());
		ReturnMsgEnum.success.setMsg("密码修改成功！");
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 切换商户状态
	 * @param request
	 * @param merchant
	 * @return
	 */
	public JSONObject updateState(Merchant merchant) {
		if (merchant == null || merchant.getId() == null) {
			ReturnMsgEnum.error.setMsg("操作失败，当前商户不存在或已被删除！");
			return ReturnMsgEnum.returnMsg();
		}
		merchant.checkState();
		merchantRepository.updateState(merchant.getId(), merchant.getState());
		ReturnMsgEnum.success.setMsg("修改成功！");
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 更新商户产品默认通道
	 * @param vo
	 * @return
	 */
	@Transactional
	public JSONObject updateMerchantProductGallery(MerchantProductGallery vo) {
		Merchant merchant = vo.getMerchant();
		List<MerchantProductGallery> list = merchantProductGalleryRepository.findListByMerchant(merchant);
		List<MerchantProductGallery> mpgs = new ArrayList<MerchantProductGallery>();
		
		Long productId = vo.getProductId();
		Optional<Product> optional = productRepository.findById(productId);
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("设置失败，产品不存在或已被删除！").toJson();
		Product product = optional.get();
		Gallery gallery = vo.getGallery();
		List<Product> children = new ArrayList<Product>();
		if (product.getType() == 0) {
			children.addAll(product.getChildren());
		}
		children.add(vo.getProduct());
		if (list == null) {
			list = new ArrayList<MerchantProductGallery>();
		}
		for (Product child : children) {
			Long pId = child.getId();
			boolean isAdd = false;
			if (!list.isEmpty()) {
				for (MerchantProductGallery mpg : list) {
					if (mpg.getProductId().equals(pId)) {
						isAdd = true;
						mpg.setGallery(gallery);
						mpg.setProduct(child);
						mpg.setMerchant(merchant);
						mpgs.add(mpg);
						break;
					}
				}
			}
			if (!isAdd)
				mpgs.add(vo.newEntityByProduct(child));
		}
		merchantProductGalleryRepository.saveAll(mpgs);
		return ReturnMsgEnum.success.setMsg("设置成功！").toJson();
	}
	
	/**
	 * 一键更新所有商户默认通道
	 * @param vo
	 * @return
	 */
	@Transactional
	public JSONObject updateMerchantProductGallerys(MerchantProductGallery vo) {
		merchantProductGalleryRepository.updateGallery(vo.getGalleryId(), vo.getProductId());
		return ReturnMsgEnum.success.setMsg("设置成功！").toJson();
	}

	/**
	 * 更新商户-产品费率
	 * @param list
	 * @return
	 */
	@Transactional
	public JSONObject updateMerchantRate(List<RateArray> list) {
		if (list != null && !list.isEmpty()) {
			List<ProductRate> productRates = new ArrayList<ProductRate>();
			RateArray rateArray = new RateArray();
			rateArray.merchantProductAndIds(list);
			List<Product> products = rateArray.getProducts();
			Merchant merchant = new Merchant(rateArray.getMerchantId());
			List<ProductRate> rates = productRateRepository.findByMerchantAndProduct(merchant, products);
			list.stream().filter(rate -> rate.validationRate()).forEach(rate -> {
				ProductRate productRate = null;
				Long merchantId = rate.getMerchantId();
				Long productId = rate.getProductId();
				if (rates != null && !rates.isEmpty()) {
					for (ProductRate entity : rates) {
						if (entity.getMerchantId().equals(merchantId) 
								&& entity.getProductId().equals(productId)) {
							productRate = entity;
							break;
						}
					}
				}
				productRates.add(rate.getProductRate(productRate));
			});
			productRateRepository.saveAll(productRates);
		}
		ReturnMsgEnum.success.setMsg("商户费率，设置成功！");
		return ReturnMsgEnum.returnMsg();
	}

	/**
	 * 根据商户账号查询商户
	 * @return
	 */
	public MerchantVo findMerchantCount() {
		return new MerchantVo(merchantRepository.findMerchantCount());
	}
	
	public Optional<Merchant> findByAccount(String account){
		return merchantRepository.findByAccount(account);
	}

}
