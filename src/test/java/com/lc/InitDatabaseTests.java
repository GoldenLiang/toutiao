package com.lc;

import com.lc.ToutiaoApplication;
import com.lc.controller.HomeController;
import com.lc.dao.CommentDAO;
import com.lc.dao.LoginTicketDAO;
import com.lc.dao.NewsDAO;
import com.lc.dao.UserDAO;
import com.lc.model.Comment;
import com.lc.model.EntityType;
import com.lc.model.LoginTicket;
import com.lc.model.Message;
import com.lc.model.News;
import com.lc.model.User;
import com.lc.service.CrawlingNewsService;
import com.lc.service.FollowService;
import com.lc.service.MessageService;
import com.lc.service.NewsService;
import com.lc.util.JedisAdapter;
import com.lc.util.ToutiaoUtil;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;

    @Autowired
    NewsDAO newsDAO;

    @Autowired
    LoginTicketDAO ticketDAO;
    
    @Autowired
    FollowService followService;
    
    @Autowired
    MessageService messageService;
    
    @Autowired
    CrawlingNewsService crawlingNewsService;
    
    @Autowired
    NewsService newsService;
    
    @Autowired
    CommentDAO commentDAO;
    
    @Autowired
    JedisAdapter jedisAdapter;
    
    @Test
    public void testJedis() {
    	User user = new User();
    	user.setHeadUrl("www.ve223.com");
    	user.setName("taotao");
    	user.setPassword("pwd");
    	user.setSalt("sa");
    	
    	jedisAdapter.setObject("userxx", user);
    	User u = jedisAdapter.getObject("userxx", User.class);
    	System.out.println(ToStringBuilder.reflectionToString(u));
    }
    
    @Test
    public void initData() {
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.lc.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            user.setPassword("newpassword");
            userDAO.updatePassword(user);
            
            for(int j = 0; j < 3; j++) {
        		followService.follow(j, 3, i);
        	}

	    	News news = new News();
	        news.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
			news.setCreatedDate(date);
			news.setImage(String.format("http://images.lc.com/head/%dm.png", random.nextInt(1000)));
			news.setLikeCount(i + 1);
			news.setUserId(i + 1);
			news.setTitle(String.format("TITLE{%d}", i));
			news.setLink(String.format("http://www.lc.com/%d.html", i));
			newsDAO.addNews(news);
            
        }

        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));
        
       
    }
    
    @Test
    public void TestMessages() {
    	String conversationId = "2_16";
    	int count = 0;
    	List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
    	for(Message msg : conversationList) {
    		messageService.addMessage(msg);
    		count++;
    		System.out.println("fromId  " + msg.getFromId());
    		System.out.println("toId  " + msg.getToId());
    		System.out.println(msg.getContent());
    	}
    	System.out.println("commments " + count);
    }
      
    @Test
    public void TestTicket() {
        for (int i = 0; i < 11; ++i) {
	    	LoginTicket loginTicket = new LoginTicket();
	        loginTicket.setStatus(0);
	        loginTicket.setUserId(i + 1);
	        loginTicket.setExpired(new Date());
	        loginTicket.setTicket(String.format("ticket%d", i + 1));
	        
	        ticketDAO.addTicket(loginTicket);
	        ticketDAO.updateTicketStatus(loginTicket.getTicket(), 2);	
        }
        
        Assert.assertEquals(1, ticketDAO.selectByTicket("TICKET1").getStatus());
        Assert.assertEquals(2, ticketDAO.selectByTicket("TICKET1").getStatus());
    }
    
    @Test
    public void TestComment() {
    	for(int i = 0; i < 3; i++) {
    		Comment comment = new Comment();
    		comment.setContent("comment " + (i + 1));
    		comment.setCreatedDate(new Date());
    		comment.setEntityId(1001 + i);
    		comment.setEntityType(EntityType.NEWS);
    		comment.setUserId(i + 1);
			commentDAO.addComment(comment );
    	}
    	
    	Assert.assertNotNull(commentDAO.selectByEntity(1001, EntityType.NEWS));
    }

    @Test 
    public void TestMessage() {
    	for(int i = 0; i < 3; i++) {
    		Message message = new Message();   
    		message.setContent("" + (i + 1));
    	}
    }
    
    @Test
    public void TestTime() {
    	Calendar calendar = Calendar.getInstance();  
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);  
        Date today = calendar.getTime();  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        String result = format.format(today);
        System.out.println(result);
        
        List<News> list = newsService.getLatestNews("2018-02-21");
        for(News news : list) {
        	System.out.println(news.getTitle());
        	System.out.println(news.getLink());
        }
    }
    
    @Test
    public void TestCrawling() {
    	crawlingNewsService.saveCrawlingNews();
    }
    
    @Test
    public void updatePassword() {
    	User user = new  User();
    	user.setSalt(UUID.randomUUID().toString().substring(0, 5));
    	user.setId(1);
    	user.setPassword(ToutiaoUtil.MD5("123456" + user.getSalt()));
    	userDAO.updatePassword(user);
    }
}


