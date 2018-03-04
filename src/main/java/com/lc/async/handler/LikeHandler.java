package com.lc.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lc.async.EventConsumer;
import com.lc.async.EventHandler;
import com.lc.async.EventModel;
import com.lc.async.EventType;
import com.lc.model.HostHolder;
import com.lc.model.Message;
import com.lc.model.User;
import com.lc.service.MessageService;
import com.lc.service.UserService;

@Component
public class LikeHandler implements EventHandler {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Override
	public void doHandle(EventModel model) {
		Message message = new Message();
		if(hostHolder == null || hostHolder.getUser() == null) { 
			return;
		}
		
		message.setFromId(hostHolder.getUser().getId());
		message.setToId(model.getActorId());
		User user = userService.getUser(model.getActorId());
		message.setContent("用户" + user.getName() + "赞了你的资讯" + "http://127.0.0.1:8080/news/" + model.getEntityId());
		message.setCreatedDate(new Date());
		message.setConversationId(message.getFromId() < message.getToId() ? String.format("%d_%d", message.getFromId(), message.getToId()) :
                String.format("%d_%d", message.getToId(), message.getFromId()));
		messageService.addMessage(message); 
	}

	@Override
	public List<EventType> getSupportTypes() {
		return Arrays.asList(EventType.LIKE);
	}

}
