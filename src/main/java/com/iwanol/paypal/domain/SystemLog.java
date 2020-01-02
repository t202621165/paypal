package com.iwanol.paypal.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 系统日志实体
 * @author leo
 *
 */
@Entity
public class SystemLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date operationDate; //操作时间
	
	private String actionDiscription; //操作描述
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Merchant merchant;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private User user;

	public SystemLog() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public String getActionDiscription() {
		return actionDiscription;
	}

	public void setActionDiscription(String actionDiscription) {
		this.actionDiscription = actionDiscription;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
	
}
