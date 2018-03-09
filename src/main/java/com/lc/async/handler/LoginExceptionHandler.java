package com.lc.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lc.async.EventHandler;
import com.lc.async.EventModel;
import com.lc.async.EventType;
import com.lc.model.HostHolder;
import com.lc.model.Message;
import com.lc.service.MessageService;
import com.lc.util.MailSender;

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
	
	@Autowired
    MailSender mailSender;
	
	@Override
	public void doHandle(EventModel model) {
		if(hostHolder.getUser() == null) {
			return ;
		}
		
		Message message = new Message();
		message.setContent("登录异常"); 
		message.setToId(hostHolder.getUser().getId());
		message.setCreatedDate(new Date());
		
		messageService.addMessage(message);
		
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆异常", "mails/welcome.html",
                map);
	}

	@Override
	public List<EventType> getSupportTypes() {
		return Arrays.asList(EventType.LOGIN);
	}

}
