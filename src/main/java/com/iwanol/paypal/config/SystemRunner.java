package com.iwanol.paypal.config;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.iwanol.paypal.base.enums.ProductsEnum;
import com.iwanol.paypal.base.enums.ResourcesEnum;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.Resource;
import com.iwanol.paypal.domain.Role;
import com.iwanol.paypal.domain.SystemSet;
import com.iwanol.paypal.domain.User;
import com.iwanol.paypal.repository.MerchantRepository;
import com.iwanol.paypal.repository.ProductRepository;
import com.iwanol.paypal.repository.ResourceRepository;
import com.iwanol.paypal.repository.RoleRepository;
import com.iwanol.paypal.repository.SystemSetRepository;
import com.iwanol.paypal.repository.UserRepository;

/**
 * 系统初始化加载器
 * @author International
 * 2018年7月3日 下午1:46:10
 */
@Component
public class SystemRunner implements CommandLineRunner{
	
	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private SystemSetRepository systemSetRepository;
	
	@Value("${com.paypal.name}")
	private  String name;
	
	@Value("${com.paypal.mark}")
	private  String mark;
	
	@Value("${com.paypal.email}")
	private  String email;
	
	@Value("${com.paypal.domain.web}")
	private String domainName; //主站域名
	
	@Value("${com.paypal.domain.pay}")
	private String payDomainName; //接口域名
	
	@Override
	public void run(String... args) throws Exception {
		logger.info("===========================================================");
		initSystemSet();
		
		logger.info("===========================================================");
		initProduct();
		
		logger.info("===========================================================");
		initPermission();
		
		logger.info("===========================================================");
		initRoleAndUser();
	}
	
	/**
	 * 初始化系统设置
	 */
	private void initSystemSet() {
		logger.info("系统设置初始化。。。");
		
		Optional<SystemSet> optional =  systemSetRepository.findByMark(mark);
		SystemSet system;
		Merchant merchant = null;
		
		if (optional.isPresent()) { // 系统设置已初始化，查看是否绑定系统商户
			system = optional.get();
			merchant = system.getMerchant();
		}else { // 未初始化系统设置
			system = new SystemSet(mark, name, domainName, payDomainName);
		}
		
		if (merchant == null) { // 未绑定系统商户
			Optional<Merchant> optional1 = merchantRepository.findByEmail(email);
			if (optional1.isPresent()) { // 邮箱已注册
				merchant = optional1.get();
			}else {
				Optional<Merchant> optional2 = merchantRepository.findByAccount(mark);
				if (optional2.isPresent()) {// 帐号已被注册
					merchant = optional2.get();
				}else {
					merchant = Merchant.getSystemMerchant(mark, name, email);
					merchantRepository.save(merchant);
				}
			}
			system.setMerchant(merchant);
			systemSetRepository.save(system);
		}
		
		logger.info("系统设置初始化完毕！");
	}
	
	/**
	 * 初始化系统角色和用户
	 */
	private void initRoleAndUser() {
		logger.info("系统角色和用户初始化。。。");
		
		Optional<Role> rOptional = roleRepository.findByRoleMark(mark);
		Role role = null;
		
		Set<User> users = new HashSet<User>();
		boolean isNewRole = false;
		if (rOptional.isPresent()) {
			role = rOptional.get();
			users = role.getUsers();
		}else {
			role = Role.getSystemRole(mark);
			isNewRole = true;
		}
		
		User user = userRepository.findByUserName(mark);
		if (user == null) {
			user = User.getSystemUser(mark,email);
			userRepository.save(user);
		}
		
		boolean isExisit = false;
		if (users != null && !users.isEmpty()) {
			for (User u : users) {
				if (u.getUserName().equals(mark)) {
					isExisit = true;
					break;
				}
			}
		}else {
			users = new HashSet<User>();
		}
		
		if (!isExisit) {
			users.add(user);
			role.setUsers(users);
		}
		
		if (isNewRole || !isExisit) {
			List<Resource> resources = resourceRepository.findAll();
			role.setResources(new HashSet<Resource>(resources));
			roleRepository.save(role);
		}
		
		logger.info("系统角色和用户初始化完毕！");
	}
	
	/**
	 * 初始化系统权限
	 */
	private void initPermission() {
		logger.info("系统资源初始化。。。");
		
		Long count = resourceRepository.count();
		if (count == 0) {
			List<Resource> defaul = ResourcesEnum.getResourceWhithChild(null, null);
			resourceRepository.saveAll(defaul);
		}
		
		logger.info("系统资源始化完毕！");
	}
	
	/**
	 * 产品初始化
	 */
	private void initProduct() {
		logger.info("产品初始化。。。");
		
		Long counts = productRepository.count();
		if (counts == 0) {
			List<Product> list = ProductsEnum.getProductWithChild(null, null);
			productRepository.saveAll(list);
		}
		
		logger.info("产品初始化完毕！");
	}
	
}
