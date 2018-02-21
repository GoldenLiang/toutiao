package com.lc.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lc.dao.LoginTicketDAO;
import com.lc.dao.UserDAO;
import com.lc.model.HostHolder;
import com.lc.model.LoginTicket;
import com.lc.model.User;

/**
 * 拦截器
 * @author lc
 *
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {
	
	@Autowired
	private LoginTicketDAO ticketDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private HostHolder hostHolder;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		String ticket = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("ticket")) {
					ticket = cookie.getValue();
					break;
				}
			}
		}
		
		if(ticket != null) {
			LoginTicket loginTicket = ticketDAO.selectByTicket(ticket);
			//如果 Ticket 为null 或 过期 或是 status 不为0，都不合法
			if(loginTicket == null || loginTicket.getExpired().before(new Date()) || 
					loginTicket.getStatus() != 0) {
				return true;
			}
			
			//当前用户设置为 Ticket 的 user_id，id 通过 username 找到
			User user = userDAO.selectById(loginTicket.getUserId());
			hostHolder.setUsers(user);
		}
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView modelAndView)
			throws Exception {
		if(modelAndView != null && hostHolder.getUser() != null)
			modelAndView.addObject("user", hostHolder.getUser());
	}
	
	/* 
	 * 类似于 try..finally.. 的 finally， 但只处理返回值为 true 的
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception modelAndView)
			throws Exception {
		hostHolder.clear();
	}	

}
