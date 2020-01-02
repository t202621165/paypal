package com.iwanol.paypal.until;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.alibaba.fastjson.JSON;

public class PageData {

	private Integer draw;
	
	private Integer start;
	
	private Integer length;
	
	private String orderColumn;
	
	private String orderDir;
	
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
		this.start = (start == null||start < 0) ? 0 : start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = (length == null||length < 0) ? 10 : length;
	}

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	public String getOrderDir() {
		return orderDir;
	}

	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	}

	public Pageable initPageable() {
		Pageable pageable = PageRequest.of(getPageNum(), this.length, Sort.by(getDirection(), this.orderColumn));
		return pageable;
	}
	
	public Pageable initPageable(Sort sort) {
		Pageable pageable = PageRequest.of(getPageNum(), this.length, sort);
		return pageable;
	}
	
	public Direction getDirection() {
		Direction direction = Sort.Direction.ASC;
		if ("desc".equalsIgnoreCase(this.orderDir)){
			direction = Sort.Direction.DESC;
		}
		return direction;
	}
	
	public Integer getPageNum() {
		return this.start/this.length;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}
