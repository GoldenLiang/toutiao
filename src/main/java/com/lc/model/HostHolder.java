package com.lc.model;

import org.springframework.stereotype.Component;

/**
 * 当前用户
 * @author lc
 *
 */
@Component
public class HostHolder {

	private static ThreadLocal<User> users = new ThreadLocal<>();
	int adminId = 1;
	
	public User getUser() {
		return users.get();
	}

	public void setUsers(User user) {
		users.set(user);
	}

	public void clear() {
		users.remove();
	}
	
	public boolean isAdmin() {
		if(getUser() != null) {
			return getUser().getId() == adminId;
		} 
		return false;
	}
}
