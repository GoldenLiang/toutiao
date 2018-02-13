package com.lc.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lc.async.EventModel;
import com.lc.async.EventProducer;
import com.lc.async.EventType;
import com.lc.model.EntityType;
import com.lc.model.HostHolder;
import com.lc.model.User;
import com.lc.model.ViewObject;
import com.lc.service.CommentService;
import com.lc.service.FollowService;
import com.lc.service.NewsService;
import com.lc.service.UserService;
import com.lc.util.ToutiaoUtil;

@Controller
public class FollowController {

	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private FollowService followService;
    
    @Autowired
    HostHolder hostHolder;
    
    @Autowired
    UserService userService;
    
    @Autowired
    CommentService commentService;
    
    @Autowired
    NewsService newsService;
    
    @Autowired
	EventProducer eventProducer;
    
    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String follow(@RequestParam("userId") int userId) {
    	if(hostHolder.getUser() == null) {
    		return ToutiaoUtil.getJSONString(999);
    	}
    	
    	if(hostHolder.getUser().getId() == userId) {
    		return ToutiaoUtil.getJSONString(998);
    	}
    	
    	boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.USER, userId);
    	
    	eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.USER).setEntityOwnerId(userId));
    	return ToutiaoUtil.getJSONString(ret ? 0 : 1, String.valueOf(
    			followService.getFolloweesCount(hostHolder.getUser().getId(), EntityType.USER)));
    }
     
    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId) {
    	if(hostHolder.getUser() == null) {
    		return ToutiaoUtil.getJSONString(999);
    	}
    	
    	boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.USER, userId);
    	
    	eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.USER).setEntityOwnerId(userId));
    	
    	return ToutiaoUtil.getJSONString(ret ? 0 : 1, String.valueOf(
    			followService.getFolloweesCount(hostHolder.getUser().getId(), EntityType.USER)));
    }
    
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(userId, EntityType.USER,  10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowersCount(userId, EntityType.USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }
    
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.USER, 0, 10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweesCount(userId, EntityType.USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }
     
    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
		List<ViewObject> userInfos = new ArrayList<>();
		for(Integer uid : userIds) {
			User user = userService.getUser(uid);
			if(user == null) {
				continue;
			}
			
			ViewObject vo = new ViewObject();
			vo.set("user", user);
			vo.set("commentCount", commentService.SelectCount(uid, EntityType.NEWS));
			vo.set("followerCount", followService.getFollowersCount(uid, EntityType.USER));
			vo.set("followeeCount", followService.getFolloweesCount(uid, EntityType.USER));
			vo.set("newsCount", newsService.getNewsCount(uid));
			if(localUserId != 0) {
				vo.set("followed", followService.isFollower(localUserId, uid, EntityType.USER));
			} else {
				vo.set("followed", false);
			}
			userInfos.add(vo);
		}
		return userInfos;
	}
}

