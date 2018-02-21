package com.lc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lc.dao.MessageDAO;
import com.lc.model.Message;

@Service
public class MessageService {

	@Autowired
	MessageDAO messageDAO;
	
	public int addMessage(Message message) {
		return messageDAO.addMessage(message);
	}

	public List<Message> getConversationList(int localUserId, int offset, int limit) {
		return messageDAO.getConversationList(localUserId, offset, limit);
	}
	
	public int getUnreadCount(int id, String conversationId) {
		return messageDAO.getUnreadCount(id, conversationId);
	}

	public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
		return messageDAO.getConversationDetail(conversationId, offset, limit);
	}

	public int getConversationCount(String conversationId) {
		return messageDAO.getConversationCount(conversationId);
	}
}
