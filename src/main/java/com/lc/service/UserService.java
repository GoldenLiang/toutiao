package com.lc.service;

import com.lc.async.EventModel;
import com.lc.async.EventProducer;
import com.lc.async.EventType;
import com.lc.dao.LoginTicketDAO;
import com.lc.dao.UserDAO;
import com.lc.model.LoginTicket;
import com.lc.model.User;
import com.lc.util.JedisAdapter;
import com.lc.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;
    
    @Autowired  
    private HttpServletRequest request;  
    
    @Autowired
    EventProducer eventProducer;
    
    @Autowired  
    JedisAdapter jedisAdapter;

    public Map<String, Object> register(String username, String password, String CaptchaCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

    	String captchaValue =  jedisAdapter.get("CaptchaCode");
        if(captchaValue == null) {  
        	map.put("msgpwd", "验证码不存在");
            return map;  
        }  
        
        if(!captchaValue.substring(1, captchaValue.length() - 1).equals(CaptchaCode)) {  
            map.put("msgcap", "验证码错误");
            return map;
        }  
        
        // 密码强度
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        String head = String.format("http://images.lc.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        user.setIp(getIpAddr(request));
        userDAO.addUser(user);
        String ticket = addLoginTicket(user);
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String username, String password, String CaptchaCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }
   
    	String captchaValue =  jedisAdapter.get("CaptchaCode");
        if (captchaValue == null) {  
        	map.put("msgpwd", "验证码不存在");
            return map;  
        }  
        
        if(!captchaValue.substring(1, captchaValue.length() - 1).equals(CaptchaCode)) {  
            map.put("msgcap", "验证码错误");
            return map;
        }  

        map.put("userId", user.getId());
        
        String ticket = addLoginTicket(user);
        if(ticket.equals("")) {
        	map.put("msgpwd", "ip地址异常，请重新验证");
        	eventProducer.fireEvent(new EventModel(EventType.LOGIN)
            		.setActorId((int) map.get("userId"))
            		.setExt("username", username)
            		.setExt("email", "xxx@qq.com"));
        	return map;
        }
        
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicket(User user) {
        LoginTicket loginTicket = new LoginTicket();
        String salt =  UUID.randomUUID().toString().substring(0, 5);
        String ticket = ToutiaoUtil.MD5(getIpAddr(request) + salt);
		loginTicket.setTicket(ticket);
        
		//验证ip
        if(!ticket.equals(ToutiaoUtil.MD5(user.getIp() + salt))) {
			return "";
		}
        
        loginTicket.setUserId(user.getId());
        loginTicket.setExpired(System.nanoTime() + 3600 * 24 * 1000);
        loginTicket.setStatus(0);
        loginTicket.setSalt(salt); 
        
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }
    
    /** 
     * 获取当前网络ip 
     */  
    public String getIpAddr(HttpServletRequest request){  
        String ipAddress = request.getHeader("x-forwarded-for");  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getRemoteAddr();  
                if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                    //根据网卡取本机配置的IP  
                    InetAddress inet=null;  
                    try {  
                        inet = InetAddress.getLocalHost();  
                    } catch (UnknownHostException e) {  
                        e.printStackTrace();  
                    }  
                    ipAddress= inet.getHostAddress();  
                }  
            }  
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
            if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
                if(ipAddress.indexOf(",")>0){  
                    ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
                }  
            }  
            return ipAddress;   
    }
    
    public User getUser(int id) {
        return userDAO.selectById(id);
    } 

    public void logout(String ticket) {
        loginTicketDAO.updateTicketStatus(ticket, 1);
    }

	public User getUserByName(String name) {
		return userDAO.selectByName(name);
	}

}
