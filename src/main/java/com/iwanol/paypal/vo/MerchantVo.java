package com.iwanol.paypal.vo;

import java.util.List;

public class MerchantVo {
	
	private Long count;
	
	private Integer state;
	
	private Long totalCount = 0L;
	
	private Long notActiveCount = 0L;
	
	private Long closeCount = 0L;
	
	public MerchantVo() {}
	
	public MerchantVo(Long count, Integer state) {
		this.count = count;
		this.state = state;
	}
	
	public MerchantVo(List<MerchantVo> list) {
		if (list != null && list.size() > 0) {
			for (MerchantVo vo : list) {
				Integer state = vo.getState();
				Long count = vo.getCount();
				if (state == 0) {
					this.notActiveCount = count;
				}
				if (state == 3) {
					this.closeCount = count;
				}
				this.totalCount += count;
			}
		}
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Long getNotActiveCount() {
		return notActiveCount;
	}

	public void setNotActiveCount(Long notActiveCount) {
		this.notActiveCount = notActiveCount;
	}

	public Long getCloseCount() {
		return closeCount;
	}

	public void setCloseCount(Long closeCount) {
		this.closeCount = closeCount;
	}

	@Override
	public String toString() {
		return "MerchantVo [count=" + count + ", state=" + state + ", totalCount=" + totalCount + ", notActiveCount="
				+ notActiveCount + ", closeCount=" + closeCount + "]";
	}
	
}
