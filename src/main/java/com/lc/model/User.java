package com.lc.model;

public class User {

	int id;
	String name;
	String password;
	String salt;
	String headUrl;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public String getSalt() {
		return salt;
	}
	public String getHeadUrl() {
		return headUrl;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
	
	
	
}
