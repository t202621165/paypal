package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.iwanol.paypal.until.DateUtil;

/**
 * 批复
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
			name = "replyWithUserAndSettlement", 
			attributeNodes = {
					@NamedAttributeNode(value = "user"),
					@NamedAttributeNode(value = "settleMents")
				}
		)
})
public class SettleMentReply implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(length=22)
	private String serialNumber; //流水号 B_+年+月+日+时+分+秒+毫秒+4位随机数+服务器标识

	@Column(nullable=false)
	private Boolean state; //0 批复中 1 出款成功
	
	@OneToMany(mappedBy="settleMentReply",cascade={CascadeType.PERSIST,CascadeType.REMOVE})
	private Set<SettleMent> settleMents = new HashSet<SettleMent>();
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date replyDate; //批复时间
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User user; //批复人
	
	@Transient
	private Long counts;
	
	@Transient
	private BigDecimal amount;
	
	@Transient
	private String userName;

	public SettleMentReply() {
		super();
	}
	
	public SettleMentReply(Long id) {
		this.id = id;
	}
	
	public SettleMentReply(User user) {
		this.user = user;
		this.state = Boolean.FALSE;
		this.replyDate = new Date();
		this.serialNumber = DateUtil.getTimeStampNumber("B_", 4);
	}
	
	public SettleMentReply(Long id,String serialNumber,Date replyDate, Boolean state, 
			Long counts, double amount, String userName) {
		this.id = id;
		this.serialNumber = serialNumber;
		this.replyDate = replyDate;
		this.state = state;
		this.counts = counts;
		this.amount = new BigDecimal(amount)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.userName = userName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Set<SettleMent> getSettleMents() {
		return settleMents;
	}

	public void setSettleMents(Set<SettleMent> settleMents) {
		this.settleMents = settleMents;
	}

	public String getReplyDate() {
		return replyDate==null?null:DateUtil.getDate(replyDate, "yyyy-MM-dd HH:mm:ss");
	}

	public void setReplyDate(Date replyDate) {
		this.replyDate = replyDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getCounts() {
		return counts;
	}

	public void setCounts(Long counts) {
		this.counts = counts;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "SettleMentReply [id=" + id + ", serialNumber=" + serialNumber + ", state=" + state + ", replyDate="
				+ replyDate + ", counts=" + counts + ", amount=" + amount + ", userName=" + userName + "]";
	}
	
}
