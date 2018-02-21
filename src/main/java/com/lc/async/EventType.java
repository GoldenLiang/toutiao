package com.lc.async;

public enum EventType {

	LIKE(0),
	COMMIT(1),
	LOGIN(2),
	MAIL(3),
	FOLLOW(4),
	UNFOLLOW(5);
	
	private int value;
	
	EventType(int value) { 
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
