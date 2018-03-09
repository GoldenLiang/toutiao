package com.lc.model;

import java.util.Date;

public class LoginTicket {
    private int id;
    private int userId;
    private long expired;
    private int status;// 0有效，1无效
    private String ticket;
    private String salt;
    
    public String getSalt() {
		return salt;
	}
    
    public void setSalt(String salt) {
		this.salt = salt;
	}

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long time) {
        this.expired = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
