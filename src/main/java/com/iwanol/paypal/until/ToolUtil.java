package com.iwanol.paypal.until;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

public class ToolUtil {
	private static ToolUtil toolUtil;
	public static ToolUtil getToolUtil(){
		if(toolUtil == null){
			return new ToolUtil();
		}
		return toolUtil;
	}
	
	/**
	 * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
	 * 实现步骤: <br>
	 * 
	 * @param paraMap
	 *            要排序的Map对象
	 * @param removeKeys
	 *            需要去除的key
	 * @param urlEncode
	 *            是否需要URLENCODE
	 * @param removeNull
	 *            是否需要去除空值
	 * @return
	 */
	public <v> String formatUrlMap(Map<String, v> paraMap, Set<String> removeKeys, boolean sort, boolean urlEncode, boolean removeNull) {
		String buff = "";
		Map<String, v> tmpMap = paraMap;
		try {
			List<Map.Entry<String, v>> infoIds = new ArrayList<Map.Entry<String, v>>(tmpMap.entrySet());
			if (sort) {
				// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
				Collections.sort(infoIds,
						new Comparator<Map.Entry<String, v>>() {
					@Override
					public int compare(Map.Entry<String, v> o1,
							Map.Entry<String, v> o2) {
						return (o1.getKey()).toString().compareTo(
								o2.getKey());
					}
				});
			}
			// 构造URL 键值对的格式
			StringBuilder buf = new StringBuilder();
			for (Map.Entry<String, v> item : infoIds) {
				if (!StringUtils.isEmpty(item.getKey())) {
					String key = item.getKey();
					String val = item.getValue().toString();
					if (removeNull) {
						if (val==null||"".equals(val)) {
							continue;
						}
					}
					if (removeKeys!=null&&removeKeys.contains(key)) {
						continue;
					}
					if (urlEncode) {
						val = URLEncoder.encode(val, "utf-8");
					}
					buf.append(key + "=" + val);
					buf.append("&");
				}

			}
			buff = buf.toString();
			if (buff.isEmpty() == false){
				buff = buff.substring(0, buff.length() - 1);
			}
		} catch (Exception e) {
			return null;
		}
		return buff;
	}
}
