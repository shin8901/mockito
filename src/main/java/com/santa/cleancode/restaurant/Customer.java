package com.santa.cleancode.restaurant;

public class Customer {

	private String name;
	private String phoneNumber;
	private String email;

	public Customer(String name,String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	public Customer(String name,String phoneNumber,String email) {
		if(!EmailValidator.isValid(email)) {
			throw new RuntimeException("email is not valid");
		}

		this.email = email;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}
}
