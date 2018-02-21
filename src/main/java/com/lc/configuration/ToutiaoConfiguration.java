package com.lc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.lc.interceptor.LoginRequiredInterceptor;
import com.lc.interceptor.PassportInterceptor;

/**
 * 将拦截器添加到链路上
 * @author lc
 *
 */
@Component
public class ToutiaoConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private PassportInterceptor passportInterceptor;
	
	@Autowired
	private LoginRequiredInterceptor loginRequiredInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(passportInterceptor);
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
		super.addInterceptors(registry);
	}

	
}
