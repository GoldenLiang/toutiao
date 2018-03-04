package com.lc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.lc.model.HostHolder;

/**
 * 权限拦截器，未登录不能查看某些页面
 * @author lc
 *
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

	@Autowired
	private HostHolder hostHandler;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		if(hostHandler.getUser() == null) {
			response.sendRedirect("/?pop=1");
			return false;
		}
		return true;
	}

}
