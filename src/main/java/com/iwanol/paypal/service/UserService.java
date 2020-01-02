package com.iwanol.paypal.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.SettleMentReply;
import com.iwanol.paypal.domain.User;
import com.iwanol.paypal.repository.NoticeRepository;
import com.iwanol.paypal.repository.ResourceRepository;
import com.iwanol.paypal.repository.SettleMentReplyRepository;
import com.iwanol.paypal.repository.SettleMentRepository;
import com.iwanol.paypal.repository.UserRepository;
import com.iwanol.paypal.security.SecurityManager;

@Service
public class UserService extends BaseServiceImpl<User,Object> implements UserDetailsService {
	
	@Value("${com.paypal.mark}")
	private String mark;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NoticeRepository noticeRepository;
	
	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private SettleMentRepository settleMentRepository;
	
	@Autowired
	private SettleMentReplyService settleMentReplyService;
	
	@Autowired
	private SettleMentReplyRepository settleMentReplyRepository;
	
	@Override
	public BaseRepository<User, Long> getRepository() {
		return userRepository;
	}

	@Override
	public Specification<User> getSpecification(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(username);
		if(user == null)
			throw new UsernameNotFoundException("**身份验证失败**");
		if (!SecurityManager.verifyPwd(user.getPassWord()))
			throw new UsernameNotFoundException("**身份验证失败**");
		
		if(!user.isAccountNonLocked()) { //账户被锁定
			SecurityManager.setValue2Session("locked","true");
			throw new UsernameNotFoundException("**身份验证失败**");
		}
		
		if (user.isAccountNonLocked())
			user.setResources(resourceRepository.findByParentOrderBySort(null));
		return user;
	}
	
	/**
	 * 更换用户名查询用户
	 * @param username
	 * @return
	 */
	public User findByUserName(String username) {
		return userRepository.findByUserName(username);
	}
	
	@Override
	public JSONObject saveEntity(User t) {
		if (t.passValidate(true)) {
			if (!addValidate(t)) {
				userRepository.save(t);
				ReturnMsgEnum.success.setMsg("用户“"+t.getUserName()+"”，添加成功！");
			}else{
				ReturnMsgEnum.error.setMsg("添加失败，当前用户已存在！");
			}
		}else{
			ReturnMsgEnum.error.setMsg(t.getMsg());
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 删除用户
	 */
	@Override
	public JSONObject deleteEntity(User t) {
		User cUser = SecurityManager.getUser();
		Long id = t.getId();
		if (cUser.getId() != id) {
			Optional<User> optional = userRepository.findById(id);
			if (!optional.isPresent())
				return ReturnMsgEnum.error.setMsg("当前用户不存在或已被删除！").toJson();
			
			User user = optional.get();
			if (user.getUserName().equals(mark))
				return ReturnMsgEnum.error.setMsg("不可删除系统默认用户！").toJson();
			
			Long count = settleMentReplyService.findReplyCountsByUser(optional.get());
			if (count > 0)
				return ReturnMsgEnum.error.setMsg("当前用户有未批付记录，不可删除！").toJson();
			
			List<SettleMentReply> list = settleMentReplyRepository.findByUser(user);
			if (list != null && !list.isEmpty())
				settleMentRepository.deleteByReplys(list);
			// 删除用户批付记录
			settleMentReplyRepository.deleteByUser(user);
			// 删除用户创建的消息
			noticeRepository.deleteByUser(user);
			// 删除用户角色信息
			userRepository.deleteUserRole(id);
			userRepository.delete(t);
			ReturnMsgEnum.success.setMsg("删除成功！");
		}else{
			ReturnMsgEnum.error.setMsg("不可删除当前登录用户！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	@Override
	public boolean addValidate(User t) {
		User user = userRepository.findByUserName(t.getUserName());
		return user != null;
	}

	/**
	 * 用户启用/禁用
	 * @param id
	 * @return
	 */
	public JSONObject updateState(Long id) {
		userRepository.updateState(id);
		ReturnMsgEnum.success.setMsg("设置成功！");
		return ReturnMsgEnum.returnMsg();
	}

	/**
	 * 用户赋予/撤销角色
	 * @param user
	 * @return
	 */
	@Transactional
	public JSONObject updateUserRoles(User user) {
		Long id = user.getId();
		Long roleId = user.getRoleId();
		userRepository.deleteUserRole(id, roleId);
		if (user.getType()) {
			userRepository.addUserRole(id, roleId);
		}
		ReturnMsgEnum.success.setMsg("设置成功！");
		return ReturnMsgEnum.returnMsg();
	}

	/**
	 * 更新用户登录密码
	 * @param user
	 * @return
	 */
	public JSONObject updateUserPassword(User user) {
		if (user.passValidate(false)) {
			userRepository.updatePassWord(user.getId(),user.getPassWord());
			ReturnMsgEnum.success.setMsg("密码修改成功！");
		}else{
			ReturnMsgEnum.error.setMsg(user.getMsg());
		}
		return ReturnMsgEnum.returnMsg();
	}

}
