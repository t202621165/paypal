package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.security.SecurityManager;
import com.iwanol.paypal.until.DateUtil;

/**
 * 消息推送实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "notcieWithUserAndMerchant", 
		attributeNodes = {
			@NamedAttributeNode(value = "user"),
			@NamedAttributeNode(value = "merchant")
		}
	)
})
public class Notice implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendDate; //消息推送日期
	
	@Column(length=25)
	private String title;
	
	@Column(columnDefinition="text")
	private String content; //消息内容

	@Column(nullable=false)
	private Boolean state; //消息状态 false/0 不启用  true/1 启用 

	@Column(nullable=false)
	private Boolean isRead; // false/0未读 true/1已读

	@Column(nullable=false)
	private Boolean sendType; // false/0  推送到平台 ture 1 推送到邮箱
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Merchant merchant; // 商户
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User user; // 系统管理员
	
	@Transient
	private Long merchantId;
	
	@Transient
	private String email;
	
	@Transient
	private boolean isChecked;
	
	@Transient
	private Long[] ids;
	
	@Transient
	private List<Notice> list = new ArrayList<Notice>();
	
	public ReturnMsgEnum validate(){
		if (title == null || "".equals(title))
			return ReturnMsgEnum.error.setMsg("消息标题不能为空！");
		
		if (title.length() < 4 || title.length() > 25)
			return ReturnMsgEnum.error.setMsg("标题在4到25个字之间！");
		
		if (content == null || "".equals(content))
			return ReturnMsgEnum.error.setMsg("标题内容不能为空！");
		
		if (user == null)
			this.user = SecurityManager.getUser();
		
		if (sendType == null)
			this.sendType = Boolean.FALSE;
		
		this.state = Boolean.TRUE;
		this.isRead = sendType;
		this.sendDate = new Date();
		return ReturnMsgEnum.success.setMsg("校验通过！");
	}

	public Notice() {
		super();
	}
	
	public Notice(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSendDate() {
		return DateUtil.getDate(sendDate, "yyyy-MM-dd HH:mm:ss");
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Boolean getSendType() {
		return sendType;
	}

	public void setSendType(Boolean sendType) {
		this.sendType = sendType;
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

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public Long[] getIds() {
		return ids;
	}

	public void setIds(Long[] ids) {
		this.ids = ids;
		if (ids != null && ids.length > 0)
			for (Long id : ids) {
				list.add(new Notice(id));
			}
	}

	public List<Notice> getList() {
		return list;
	}

	public void setList(List<Notice> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "Notice [id=" + id + ", sendDate=" + sendDate + ", title=" + title + ", content=" + content + ", state="
				+ state + ", isRead=" + isRead + ", sendType=" + sendType + "]";
	}
	
}
