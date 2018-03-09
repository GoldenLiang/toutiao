package com.lc.controller;

import com.lc.model.Comment;
import com.lc.model.EntityType;
import com.lc.model.HostHolder;
import com.lc.model.News;
import com.lc.model.User;
import com.lc.model.ViewObject;
import com.lc.service.CommentService;
import com.lc.service.FollowService;
import com.lc.service.LikeService;
import com.lc.service.NewsService;
import com.lc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;
    
    @Autowired
    FollowService followService;
    
    @Autowired
	HostHolder hostHolder;

    @Autowired
	LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        List<ViewObject> vos = getVos(newsList);
        return vos; 
    }  
    
    public List<ViewObject> getNews(int past) {  	
    	Calendar calendar =	Calendar.getInstance();
    	calendar.add(Calendar.DATE, -past);    //得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	String result = df.format(date);
    	List<News> newsList = newsService.getLatestNews(result);
    	
    	newsList = newsService.setAllNewsScore(newsList);
    	//newsList = newsService.rankingNews(newsList);
    	List<ViewObject> vos = getVos(newsList);
    	return vos; 
    }
    
    private List<ViewObject> getVos(List<News> newsList) {
    	int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
    	
    	List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            vo.set("followCount", followService.getFollowersCount(news.getId(), EntityType.USER));
            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            } 
            vos.add(vo);
        }
        return vos;
	}

	private List<ViewObject> getComments(int userId) {
        List<Comment> commentsList = commentService.getUserComments(userId);
        
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentsList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            vo.set("user", userService.getUser(comment.getUserId()));
            vo.set("newsTitle", newsService.getNewsTitle(comment.getEntityId()));
            
            vos.add(vo);
        } 
        return vos; 
    }  
    
    private ViewObject profileUser(int userId) {
    	User user = userService.getUser(userId);
    	ViewObject vo = new ViewObject();
    	vo.set("user", user);
    	vo.set("commentCount", commentService.selectUserCount(userId));
    	vo.set("followerCount", followService.getFollowersCount(userId, EntityType.USER));
    	vo.set("followeeCount", followService.getFolloweesCount(userId, EntityType.USER));
    	vo.set("newsCount", newsService.getNewsCount(userId));
    	if(hostHolder.getUser() != null) {
    		vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), userId, EntityType.USER));
    	} else {
    		vo.set("followed", false);
    	} 
    	  
    	return vo;
    }
     
    @RequestMapping(path = {"/user/{userId}/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
    	ViewObject vo = profileUser(userId); 
    	  
    	model.addAttribute("profileUser", vo);
    	model.addAttribute("vos", getNews(userId, 0, 10));
        return "profile";   
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(0, 0, 30));
        model.addAttribute("pop", pop);
        
        if(hostHolder.isAdmin()) {
        	return "admin";
        }
        
        return "home";
    }
     
    @RequestMapping(path = {"/user/{userId}/news"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userNews(Model model, @PathVariable("userId")int userId) {
    	model.addAttribute("vos", getNews(userId, 0, 10));
    	
    	return "home";
    }
     
    @RequestMapping(path = {"/user/{userId}/comments"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userComments(Model model, @PathVariable("userId")int userId) {
    	model.addAttribute("comments", getComments(userId));
    	model.addAttribute("profileUser", profileUser(userId));
    	 
    	return "comment";
    }
    
    @RequestMapping(path = {"/ranking/daily"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String dailyChart(Model model) {
    	//日榜
    	model.addAttribute("vos", getNews(1));
    	
    	return "ranking";
    }
    
    @RequestMapping(path = {"/ranking/weekly"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String weeklyChart(Model model) {
    	//周榜
    	model.addAttribute("vos", getNews(7));
    	
    	return "ranking";
    }
}
