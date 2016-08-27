package com.algorithm.util;

import java.util.ArrayList;

/**
 * 单个元素实体类
 * 
 * @author chenjingyang
 *
 */
public class Element implements Comparable<Element> {
	private int cost;
	private ArrayList<Integer> path;

	public Element(int cost, ArrayList<Integer> path) {
		this.cost = cost;
		this.path = path;
	}

	public Element() {
		super();
	}

	public int getKey() {
		return cost;
	}
	public void setKey(int key) {
		this.cost = key;
	}
	public ArrayList<Integer> getValue() {
		return path;
	}
	public void setValue(ArrayList<Integer> value) {
		this.path = value;
	}

	@Override
	public int compareTo(Element e) {
		// 从小到大排序(快速排序算法)
		return this.getKey() - e.getKey();
	}

}
