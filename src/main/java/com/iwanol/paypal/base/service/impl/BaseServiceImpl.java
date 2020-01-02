package com.iwanol.paypal.base.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.BaseService;
import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.until.PageData;
@Service
public abstract class BaseServiceImpl<T,V> implements BaseService<T,V>{
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private JavaMailSender sender;
	
	@Value("${spring.mail.username}")
	private String from;
	
	/**
	 * 子类实现-获取对应持久层对象
	 * @return
	 */
	public abstract BaseRepository<T, Long> getRepository();
	
	/**
	 * 数据新增校验
	 * @param t
	 * @return
	 */
	public abstract boolean addValidate(T t);
	
	/**
	 * 复杂条件查询 动态获取查询条件
	 * @param v
	 * @return
	 */
	public abstract Specification<T> getSpecification(V v);
	
	/**
	 * 发送纯文本邮件
	 * @param to
	 * @param subject
	 * @param content
	 */
	public boolean sendSimpleEmail(String[] to, String subject, String content) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			// 设置发件人
			message.setFrom(from);
			// 设置收件人
			message.setTo(to);
			// 设置邮件主题
			message.setSubject(subject);
			message.setText(content);
			sender.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Transactional
	@Override
	public JSONObject saveEntity(T t) {
		if (!addValidate(t)) {
			getRepository().save(t);
			ReturnMsgEnum.success.setMsg("添加成功！");
		}else{
			ReturnMsgEnum.error.setMsg("系统已存在当前记录！");
		}
		return ReturnMsgEnum.returnMsg();
	}

	@Override
	public JSONObject updateEntity(T t, boolean pass) {
		if (pass) {
			ReturnMsgEnum.error.setMsg("修改失败，修改对象不存在！");
		}else{
			if (addValidate(t))
				return ReturnMsgEnum.error.setMsg("修改失败，记录已存在！").toJson();
			getRepository().save(t);
			ReturnMsgEnum.success.setMsg("修改成功！");
		}
		return ReturnMsgEnum.returnMsg();
	}

	@Transactional
	@Override
	public JSONObject deleteEntity(Long id) {
		getRepository().deleteById(id);
		ReturnMsgEnum.success.setMsg("删除成功！");
		return ReturnMsgEnum.returnMsg();
	}

	@Transactional
	@Override
	public JSONObject deleteEntity(T t) {
		getRepository().delete(t);
		ReturnMsgEnum.success.setMsg("删除成功！");
		return ReturnMsgEnum.returnMsg();
	}

	@Transactional
	@Override
	public void deleteEntityInBatch(List<T> t) {
		 getRepository().deleteAll(t);
	}

	@Override
	public T findEntity(Long id) {
		Optional<T> optional = getRepository().findById(id);
		if (optional.isPresent())
			return optional.get();
		return  null;
	}

	@Override
	public List<T> findEntitys(List<Long> ids) {
		return  getRepository().findAllById(ids);
	}

	@Override
	public List<T> findEntitys() {
		return  getRepository().findAll();
	}

	/**
	 * 无条件分页查询
	 */
	@Override
	public Map<String, Object> findEntityByPage(PageData pageData) {
		Page<T> page = getRepository().findAll(pageData.initPageable());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("draw", pageData.getDraw());
		map.put("recordsTotal", page.getTotalElements());//数据总条数
		map.put("recordsFiltered", page.getTotalElements());//显示的条数
		map.put("data", page.getContent());//数据集合
		return map;
	}
	
	/**
	 * 复杂条件分页查询
	 */
	@Override
	public Map<String, Object> findEntityBySpec(PageData pageData,V v) {
		Page<T> page = getRepository().findAll(getSpecification(v), pageData.initPageable());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("draw", pageData.getDraw());
		map.put("recordsTotal", page.getTotalElements());//数据总条数
		map.put("recordsFiltered", page.getTotalElements());//显示的条数
		map.put("data", page.getContent());//数据集合
		return map;
	}
	
	/**
	 * 自定义查询对象分页查询
	 */
	@Override
	public Map<String, Object> findPageVo(BaseVo vo,Boolean isSumData) {
		vo.setEntityManager(entityManager);
		Long totalCounts = vo.findTotalCountByQl();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("draw", vo.getDraw());
		map.put("recordsTotal", totalCounts);//数据总条数
		map.put("recordsFiltered", totalCounts);//显示的条数
		map.put("data", vo.findListByQl(true));//数据集合
		if (isSumData) {
			map.put("sumData", vo.findSumDataByQl());
		}
		return map;
	}

}
