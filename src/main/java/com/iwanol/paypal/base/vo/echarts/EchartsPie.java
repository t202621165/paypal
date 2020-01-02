package com.iwanol.paypal.base.vo.echarts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwanol.paypal.vo.OrderVo;

public class EchartsPie {

	private List<String> legendData = new ArrayList<String>();
	
	private Map<String, Boolean> selected = new HashMap<String, Boolean>();
	
	private List<Map<String, Object>> seriesData = new ArrayList<Map<String,Object>>();
	
	private List<List<Object>> seriesDatas = new ArrayList<List<Object>>();
	
	private List<String> xAxisData = new ArrayList<String>();
	
	public EchartsPie() {
		
	}
	public EchartsPie(List<OrderVo> list, boolean isTrue) {
		if (list != null && !list.isEmpty()) {
			List<Object> data1 = new ArrayList<Object>();
			List<Object> data2 = new ArrayList<Object>();
			List<Object> data3 = new ArrayList<Object>();
			List<Object> data4 = new ArrayList<Object>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> data = new HashMap<String, Object>();
				OrderVo vo = list.get(i);
				String name = vo.getProductName();
				BigDecimal amount = vo.getTotalAmount();
				legendData.add(name);
				selected.put(name, i<10);
				data.put("name", name);
				data.put("value", amount);
				seriesData.add(data);
				if (isTrue) {
					xAxisData.add(name);
					data1.add(amount);
					data2.add(vo.getTotalMerchantProfits());
					data3.add(vo.getTotalPlatformProfits());
					data4.add(vo.getTotalCount());
				}
			}
			seriesDatas.add(data1);
			seriesDatas.add(data2);
			seriesDatas.add(data3);
			seriesDatas.add(data4);
		}
	}
	
	public EchartsPie(List<OrderVo> list, String[] dates) {
			List<Object> data1 = new ArrayList<Object>();
			List<Object> data2 = new ArrayList<Object>();
			List<Object> data3 = new ArrayList<Object>();
			List<Object> data4 = new ArrayList<Object>();
			for (String date : dates) {
				xAxisData.add(date);
				BigDecimal amount = new BigDecimal(0.00);
				BigDecimal merchantProfits = new BigDecimal(0.00);
				BigDecimal platformProfits = new BigDecimal(0.00);
				Long count = 0L;
				if (list != null && !list.isEmpty()) {
					for (OrderVo vo : list) {
						String orderDate = vo.getOrderDate();
						if (date.equals(orderDate)) {
							amount = vo.getTotalAmount();
							merchantProfits = vo.getTotalMerchantProfits();
							platformProfits = vo.getTotalPlatformProfits();
							count = vo.getTotalCount();
						}
					}
				}
				data1.add(amount);
				data2.add(merchantProfits);
				data3.add(platformProfits);
				data4.add(count);
			}
			seriesDatas.add(data1);
			seriesDatas.add(data2);
			seriesDatas.add(data3);
			seriesDatas.add(data4);
	}

	public List<String> getLegendData() {
		return legendData;
	}

	public void setLegendData(List<String> legendData) {
		this.legendData = legendData;
	}

	public Map<String, Boolean> getSelected() {
		return selected;
	}

	public void setSelected(Map<String, Boolean> selected) {
		this.selected = selected;
	}

	public List<Map<String, Object>> getSeriesData() {
		return seriesData;
	}

	public void setSeriesData(List<Map<String, Object>> seriesData) {
		this.seriesData = seriesData;
	}

	public List<List<Object>> getSeriesDatas() {
		return seriesDatas;
	}
	public void setSeriesDatas(List<List<Object>> seriesDatas) {
		this.seriesDatas = seriesDatas;
	}
	public List<String> getxAxisData() {
		return xAxisData;
	}
	public void setxAxisData(List<String> xAxisData) {
		this.xAxisData = xAxisData;
	}
	@Override
	public String toString() {
		return "EchartsPie [legendData=" + legendData + ", selected=" + selected + ", seriesData=" + seriesData + "]";
	}
	
}
