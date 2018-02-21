package com.lc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lc.async.EventModel;
import com.lc.async.EventProducer;
import com.lc.async.EventType;
import com.lc.model.EntityType;
import com.lc.model.HostHolder;
import com.lc.model.News;
import com.lc.service.LikeService;
import com.lc.service.NewsService;
import com.lc.util.ToutiaoUtil;

@Controller
public class LikeController {

	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	
	@Autowired
	HostHolder hostHolder;
 	
	@Autowired
	LikeService likeService;
	 
	@Autowired
	NewsService newsService;
	
	@Autowired
	EventProducer eventProducer;
	  
	@RequestMapping(path = {"/like"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String like(@RequestParam("newsId") int newsId) {
		if(hostHolder.getUser() == null) {
			logger.error("尚未登录");
			return ToutiaoUtil.getJSONString(1, "尚未登录");
		}
		
		int userId = hostHolder.getUser().getId();
		long likeCount = likeService.like(userId, EntityType.NEWS, newsId);
		newsService.updateLikeCount(newsId, (int)likeCount);
		 
		News news = newsService.getById(newsId);
		eventProducer.fireEvent(new EventModel(EventType.LIKE)
				.setEntityOwnerId(news.getUserId())
				.setActorId(hostHolder.getUser().getId()).setEntityId(newsId));	
		return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
	}
	
	@RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public String dislike(@RequestParam("newsId") int newsId) {
		if(hostHolder.getUser() == null) {
			logger.error("尚未登录");
			return ToutiaoUtil.getJSONString(1, "尚未登录");
		}
		
		int userId = hostHolder.getUser().getId();
		long dislikeCount = likeService.dislike(userId, EntityType.NEWS, newsId);
		newsService.updateLikeCount(newsId, (int)dislikeCount);
		
		return ToutiaoUtil.getJSONString(0, String.valueOf(dislikeCount));
	}
}
