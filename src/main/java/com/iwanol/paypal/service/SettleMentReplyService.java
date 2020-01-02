package com.iwanol.paypal.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.SettleMentReply;
import com.iwanol.paypal.domain.User;
import com.iwanol.paypal.repository.SettleMentReplyRepository;
import com.iwanol.paypal.repository.SettleMentRepository;

@Service
public class SettleMentReplyService extends BaseServiceImpl<SettleMentReply, Object>{

	@Autowired
	private SettleMentRepository settleMentRepository;
	@Autowired
	private SettleMentReplyRepository settleMentReplyRepository;
	
	@Override
	public BaseRepository<SettleMentReply, Long> getRepository() {
		return settleMentReplyRepository;
	}

	@Override
	public Specification<SettleMentReply> getSpecification(Object v) {
		
		return null;
	}

	@Override
	public boolean addValidate(SettleMentReply t) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 查询用户未批付记录
	 * @param reply
	 * @return
	 */
	@SuppressWarnings("serial")
	public Long findReplyCountsByUser(User user) {
		return settleMentReplyRepository.count(new Specification<SettleMentReply>() {
			@Override
			public Predicate toPredicate(Root<SettleMentReply> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				Predicate[] predicates = new Predicate[2];
				
				predicates[0] = cb.equal(root.get("state").as(Boolean.class), Boolean.FALSE);
				predicates[1] = cb.equal(root.get("user").as(User.class), user);
				
				return cb.and(predicates);
			}
		});
	}

	@Transactional
	public JSONObject updateReplyState(SettleMentReply reply) {
		settleMentReplyRepository.updateState(reply.getId());
		settleMentRepository.updateStateByReply(reply);
		ReturnMsgEnum.success.setMsg("付款成功！");
		return ReturnMsgEnum.returnMsg();
	}

}
