package com.iwanol.paypal.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Notice;
import com.iwanol.paypal.domain.User;
import com.iwanol.paypal.repository.MerchantRepository;
import com.iwanol.paypal.repository.NoticeRepository;
import com.iwanol.paypal.security.SecurityManager;
import com.iwanol.paypal.until.VerifyUtil;

@Service
public class NoticeService extends BaseServiceImpl<Notice, Notice> {
	
	@Autowired
	private NoticeRepository noticeRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	
	@Value("${com.paypal.mark}")
	private String mark;

	@Override
	public BaseRepository<Notice, Long> getRepository() {
		return noticeRepository;
	}

	@Override
	public boolean addValidate(Notice t) {
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	public Specification<Notice> getSpecification(Notice v) {
		return new Specification<Notice>() {

			@Override
			public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				Boolean sendType = v.getSendType();
				if (sendType != null)
					list.add(cb.equal(root.get("sendType").as(Boolean.class), sendType));
				
				Long merchantId = v.getMerchantId();
				if (merchantId != null)
					list.add(cb.equal(root.get("merchant").get("id").as(Long.class), merchantId));
				
				if (!SecurityManager.hasRole(mark))
					list.add(cb.equal(root.get("user").as(User.class), SecurityManager.getUser()));
				
				Predicate[] predicates = new Predicate[list.size()];
				predicates = list.toArray(predicates);
				return cb.and(predicates);
			}
		};
	}
	
	/**
	 * 添加新通知
	 * @param notice
	 * @return
	 */
	@Transactional
	public JSONObject addNotice(Notice notice) {
		notice.validate();
		if (!ReturnMsgEnum.isSucc)
			return ReturnMsgEnum.returnMsg();
		noticeRepository.save(notice);
		if (notice.getSendType()) { // 消息推送到邮箱
			String email = notice.getEmail();
			String subject = notice.getTitle();
			String content = notice.getContent();
			String[] emails = {};
			if (VerifyUtil.isEmail(email))
				emails = new String[]{email};
			// 邮件群发
			if (StringUtils.isEmpty(email)) {
				List<String> list = merchantRepository.findEmailByState(1);
				if (list != null && !list.isEmpty()) {
					emails = new String[list.size()];
					list.toArray(emails);
					
				}
			}
			if (emails.length > 0) {
				boolean isSend = sendSimpleEmail(emails, subject, content);
				if (isSend)
					return ReturnMsgEnum.success.setMsg("添加成功！已成功将消息推送至商户邮箱。").toJson();
			}
		}
		return ReturnMsgEnum.success.setMsg("消息添加成功！").toJson();
	}
	
	public List<Merchant> findAllMerchant() {
		return noticeRepository.findAllMerchant();
	}

	/**
	 * 
	 * @param notice
	 * @return
	 */
	public JSONObject updateState(Notice notice) {
		noticeRepository.updateState(notice.getId());
		ReturnMsgEnum.success.setMsg(notice.isChecked()?"通道已启用":"通道已禁用！");
		return ReturnMsgEnum.returnMsg();
	}

	/**
	 * 批量删除消息
	 * @param notice
	 * @return
	 */
	public JSONObject deleteNotice(Notice notice) {
		Long id = notice.getId();
		if (id != null)
			noticeRepository.deleteById(id);
		
		Long[] ids = notice.getIds();
		if (ids != null && ids.length > 0)
			noticeRepository.deleteInBatch(notice.getList());
		
		return ReturnMsgEnum.success.setMsg("删除成功！").toJson();
	}

}
