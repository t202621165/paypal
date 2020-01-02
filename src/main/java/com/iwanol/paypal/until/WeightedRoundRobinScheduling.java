package com.iwanol.paypal.until;

import java.math.BigInteger;
import java.util.List;

import com.iwanol.paypal.domain.Gallery;

/**
 * 权重轮询调度算法(WeightedRound-RobinScheduling)
 * @author leo
 *
 */
public class WeightedRoundRobinScheduling {
	private static int currentIndex = -1; //上一次选择的通道
	private static int currentWeight = 0; //当前调度的权值
	private static int maxWeight = 0; //最大权值
	private static int gcdWeight = 0; //所有通道权重的最大公约数
	private static int galleryCount = 0; // 通道数量
	private static List<Gallery> galleryList; //通道集合
	
	public static void init(List<Gallery> galleryList){
		currentIndex = -1;
		currentWeight = 0;
		galleryCount = galleryList.size();
		maxWeight = getMaxWeightForGallery(galleryList);
		gcdWeight = getGCDForGallery(galleryList);
	}
	
	/**
	 * 返回最大公约数
	 * @param a
	 * @param b
	 * @return
	 */
	private static int gcd(int a, int b){
		BigInteger b1 = new BigInteger(String.valueOf(a));
		BigInteger b2 = new BigInteger(String.valueOf(b));
		BigInteger gcd = b1.gcd(b2);
		return gcd.intValue();
	}
	
	/**
	 * 返回所有通道权重的最大公约数
	 * @param galleryList
	 * @return
	 */
	private static int getGCDForGallery(List<Gallery> galleryList){
		int w = 0;
		for (int i = 0, len = galleryList.size(); i < len - 1; i++) {
			if (w == 0) {
				w = gcd(galleryList.get(i).getWeight(), galleryList.get(i + 1).getWeight());
			} else {
				w = gcd(w, galleryList.get(i + 1).getWeight());
			}
		}
		return w;
	}
	
	private static int getMaxWeightForGallery(List<Gallery> galleryList){
		int w = 0;
		for (int i = 0, len = galleryList.size(); i < len - 1; i++) {
			if (w == 0) {
				w = Math.max(galleryList.get(i).getWeight(), galleryList.get(i + 1).getWeight());
			} else {
				w = Math.max(w, galleryList.get(i + 1).getWeight());
			}
		}
		return w;
	}
	
	/**
	 * 算法流程： 假设有一组通道 S = {S0, S1, …, Sn-1} 有相应的权重，变量currentIndex表示上次选择的通道
	 * 权值currentWeight初始化为0，currentIndex初始化为-1 ，当第一次的时候返回 权值取最大的那个通道， 通过权重的不断递减
	 * 寻找 适合的通道返回，直到轮询结束，权值返回为0
	 */
	public static Gallery GetGallery() {
		while (true) {
			currentIndex = (currentIndex + 1) % galleryCount;
			if (currentIndex == 0) {
				currentWeight = currentWeight - gcdWeight;
				if (currentWeight <= 0) {
					currentWeight = maxWeight;
					if (currentWeight == 0)
						return null;
				}
			}
			if (galleryList.get(currentIndex).getWeight() >= currentWeight) {
				return galleryList.get(currentIndex);
			}
		}
	}
	
}
