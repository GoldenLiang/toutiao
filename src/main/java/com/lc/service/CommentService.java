package com.lc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lc.dao.CommentDAO;
import com.lc.model.Comment;

@Service
public class CommentService {

	@Autowired
	private CommentDAO commentDAO;
	
	public List<Comment> getCommentsByEntity(int entityId, int entityType) {
		return commentDAO.selectByEntity(entityId, entityType);
	}
	
	public int SelectCount(int entityId, int entityType) {
		return commentDAO.selectCount(entityId, entityType);
	}
	
	public int selectUserCount(int userId) {
		return commentDAO.selectUserCount(userId);
	}
	 
	public int addComment(Comment comment) {
		return commentDAO.addComment(comment);
	}

	public List<Comment> getUserComments(int userId) {
		return commentDAO.getUserComments(userId);
	}
}
