package com.lc.model;

import java.util.Date;

public class News {

	int id;
	String title;
	int userId;
	String link;
	String content;
	Date createdDate;
	String image;
	
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public int getUserId() {
		return userId;
	}
	public String getLink() {
		return link;
	}
	public String getContent() {
		return content;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public String getImage() {
		return image;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
}
