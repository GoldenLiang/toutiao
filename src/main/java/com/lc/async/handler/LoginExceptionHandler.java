package com.lc.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lc.async.EventHandler;
import com.lc.async.EventModel;
import com.lc.async.EventType;
import com.lc.model.HostHolder;
import com.lc.model.Message;
import com.lc.service.MessageService;

/**
 * 登录异常
 * @author lc
 *
 */  
  
@Component 
public class LoginExceptionHandler implements EventHandler {

	@Autowired 
	MessageService messageService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Override
	public void doHandle(EventModel model) {
		if(hostHolder.getUser() == null) {
			return ;
		}
		
		//TODO: 判断IP地址
		Message message = new Message();
		message.setContent("登录异常"); 
		message.setToId(hostHolder.getUser().getId());
		message.setCreatedDate(new Date());
		
		//messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportTypes() {
		return Arrays.asList(EventType.LOGIN);
	}

}
