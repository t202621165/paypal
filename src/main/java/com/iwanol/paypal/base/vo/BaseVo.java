package com.iwanol.paypal.base.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.until.DateUtil;

public abstract class BaseVo {
	
	public final static StringBuffer COUNT_BUF = new StringBuffer("SELECT COUNT(*)");

	@JsonIgnore
	public StringBuffer SELECT_BUF = new StringBuffer();

	@JsonIgnore
	public StringBuffer SUM_BUF = new StringBuffer();
	
	@JsonIgnore
	public StringBuffer FROM_BUF = new StringBuffer();

	@JsonIgnore
	public StringBuffer ORDER_BY_BUF = new StringBuffer(" ORDER BY temp.");
	
	@JsonIgnore
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	
	@JsonIgnore
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endDate;

	private static EntityManager entityManager;
	
	@JsonIgnore
	private Integer draw;

	@JsonIgnore
	private Integer start;

	@JsonIgnore
	private Integer length;

	@JsonIgnore
	private String orderColumn;

	@JsonIgnore
	private String orderDir;
	
	/**
	 * 设置查询参数
	 * @param query
	 */
	public abstract void setParameter(Query query);

	@JsonIgnore
	public abstract Class<?> getListObjClass();

	@JsonIgnore
	public abstract StringBuffer getQueryWhereBuf();

	@JsonIgnore
	public abstract StringBuffer getQueryGroupByBuf();
	
	/**
	 * 获取总记录数QL
	 * @return
	 */
	@JsonIgnore
	public StringBuffer getQueryCountQl(){
		StringBuffer bf = new StringBuffer();
		bf.append(COUNT_BUF);
		bf.append(FROM_BUF);
		bf.append(getQueryWhereBuf());
		return bf;
	}
	
	/**
	 * 获取统计汇总QL
	 * @return
	 */
	@JsonIgnore
	public StringBuffer getQuerySumQl() {
		StringBuffer bf = new StringBuffer();
		bf.append(SUM_BUF);
		bf.append(FROM_BUF);
		bf.append(getQueryWhereBuf());
		bf.append(getQueryGroupByBuf());
		return bf;
	}
	
	/**
	 * 获取分页列表QL
	 * @return
	 */
	@JsonIgnore
	public StringBuffer getQueryListQl(){
		StringBuffer bf = new StringBuffer();
		bf.append(SELECT_BUF);
		bf.append(FROM_BUF);
		bf.append(getQueryWhereBuf());
		bf.append(ORDER_BY_BUF);
		return bf;
	}
	
	/**
	 * 获取总记录数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public Long findTotalCountByQl(){
		try {
			StringBuffer buf = getQueryCountQl();
			if (buf == null)
				return 0L;
			Query query = entityManager.createQuery(buf.toString(),Long.class);
			setParameter(query);
			List<Long> list = query.getResultList();
			if (list == null || list.isEmpty()) {
				return 0L;
			}else if (list.size() > 1){
				return new Long(list.size());
			}
			return list.get(0);
		} catch (Exception e) {
			return 0L;
		}finally {
			entityManager.close();
		}
	}
	
	/**
	 * 获取分页数据列表
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public List findListByQl(boolean isPagging){
		try {
			StringBuffer buf = getQueryListQl();
			if (buf == null)
				return new ArrayList();
			Query query = entityManager.createQuery(buf.toString(),getListObjClass());
			setParameter(query);
			if (isPagging) {
				query.setFirstResult(this.start);
				query.setMaxResults(this.length);
			}
			return query.getResultList();
		} catch (Exception e) {
			return new ArrayList();
		}finally {
			entityManager.close();
		}
	}
	
	public Object findOneByQl(String ql){
		try {
			Query query = entityManager.createQuery(ql,getListObjClass());
			setParameter(query);
			Object obj = null;
			obj = query.getSingleResult();
			return obj;
		} catch (Exception e) {
			return null;
		}finally {
			entityManager.close();
		}
		
	}
	
	@Modifying
	@Transactional
	public int updateByQl(String ql) {
		try {
			Query query = entityManager.createQuery(ql);
			return query.executeUpdate();
		} catch (Exception e) {
			return 0;
		}finally {
			entityManager.flush();
			entityManager.close();
		}
	}
	
	@Modifying
	@Transactional
	public int updateByNativeQl(String ql) {
		try {
			Query query = entityManager.createNativeQuery(ql);
			return query.executeUpdate();
		} catch (Exception e) {
			return 0;
		}finally {
			entityManager.flush();
			entityManager.close();
		}
	}
	
	/**
	 * 获取统计汇总数据
	 * @param pageData
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public List findSumDataByQl(){
		StringBuffer buf = getQuerySumQl();
		if (buf == null)
			return null;
		Query query = entityManager.createQuery(buf.toString(),this.getClass());
		setParameter(query);
		return query.getResultList();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		try {
			this.startDate = DateUtil.getDate(startDate, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			this.startDate = DateUtil.getDateZeroPoint(new Date());
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		try {
			this.endDate = DateUtil.getDate(endDate, "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			this.endDate = new Date();
		}
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
		if (orderColumn != null && !"".equals(orderColumn)){
			ORDER_BY_BUF.append(orderColumn);
		}
	}

	public String getOrderDir() {
		return orderDir;
	}

	public void setOrderDir(String orderDir) {
		if ("ASC".equalsIgnoreCase(orderDir) || 
				"DESC".equalsIgnoreCase(orderDir)){
			ORDER_BY_BUF.append(" "+orderDir);
			this.orderDir = orderDir;
		}else{
			ORDER_BY_BUF.append(" DESC");
			this.orderDir = "DESC";
		}
		
	}
	
	@JsonIgnore
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		if (BaseVo.entityManager == null)
			BaseVo.entityManager = entityManager;
	}

	@Override
	public String toString() {
		return "BaseVo [startDate=" + startDate + ", endDate=" + endDate + ", draw=" + draw + ", start=" + start
				+ ", length=" + length + ", orderColumn=" + orderColumn + ", orderDir=" + orderDir + "]";
	}

}
