package com.example.logisticsprivacysystem.Order;

/**
 * Created by Axes on 2017/5/21.
 */

public class Order {
	private String no;
	private String num;


	public Order(String no, String num) {
		this.no = no;
		this.num = num;
	}

	public String getNo() {
		return no;
	}

	public String getNum() {
		return num;
	}
}