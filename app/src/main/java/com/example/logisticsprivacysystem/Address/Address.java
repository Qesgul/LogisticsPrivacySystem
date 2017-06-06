package com.example.logisticsprivacysystem.Address;

/**
 * Created by Axes on 2017/5/9.
 */

public class Address {
	private String name;
	private String phone;
	private String address;
	private String code;

	public Address(String name, String phone,String address,String code) {
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}
	public String getAddress() {
		return address;
	}
	public String getCode() {
		return code;
	}

}
