package com.lc.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lc.model.HostHolder;
import com.lc.model.Message;
import com.lc.model.User;
import com.lc.model.ViewObject;
import com.lc.service.MessageService;
import com.lc.service.UserService;
import com.lc.util.ToutiaoUtil;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;
    
    @Autowired
    HostHolder hostHolder;
    
    @Autowired
    UserService userService;
    
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                                   @RequestParam("toId") int toId,
                                   @RequestParam("content") String content) {
        Message msg = new Message();
        msg.setContent(content);
        msg.setCreatedDate(new Date());
        msg.setToId(toId);
        msg.setFromId(fromId);
        msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) :
                String.format("%d_%d", toId, fromId));
        messageService.addMessage(msg);
        return ToutiaoUtil.getJSONString(msg.getId());
    }
    
    
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String messageList(Model model) {
    	try {
    		int localUserId = hostHolder.getUser().getId();
			List<Message> conversationList = messageService.getConversationList(localUserId, 0, 20);
			List<ViewObject> conversations = new ArrayList<>();
			for(Message message : conversationList) {
				ViewObject vo = new ViewObject();
				vo.set("conversation", message);
				vo.set("conversationCount", messageService.getConversationCount(message.getConversationId()));
				int targetId = message.getFromId() == localUserId ?  
						message.getToId() : message.getFromId();
				User user = userService.getUser(targetId);
				vo.set("unreadCount", messageService.getUnreadCount(localUserId, message.getConversationId()));
				vo.set("user", user);
			  
				conversations.add(vo);
			}
			model.addAttribute("conversations", conversations);
		} catch (Exception e) {
			logger.error("请求消息失败" + e.getMessage());
		}
    	return "letter";
    }
    
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String messageDetail(Model model, @RequestParam("conversationId") String conversationId) {
    	try {
    		List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
    		List<ViewObject> conversations = new ArrayList<>();
    		for(Message message : conversationList) {
    			ViewObject vo = new ViewObject();
    			vo.set("message", message);
    			User user = userService.getUser(message.getFromId());
    			if(user == null) {
    				continue;
    			}
    			vo.set("headUrl", user.getHeadUrl());
    			vo.set("userName", user.getName());
    			conversations.add(vo);
    		}
    		 model.addAttribute("messages", conversations);
             return "letterDetail";
    	} catch (Exception e) {
			logger.error("请求消息失败" + e.getMessage());
		}
    	return "letterDetail";
    }
}
