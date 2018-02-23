package com.lc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import com.lc.model.Comment;
import com.lc.model.EntityType;
import com.lc.model.HostHolder;
import com.lc.model.News;
import com.lc.model.ViewObject;
import com.lc.service.CommentService;
import com.lc.service.LikeService;
import com.lc.service.NewsService;
import com.lc.service.SensitiveService;
import com.lc.service.UserService;
import com.lc.util.ToutiaoUtil;

@Controller
public class NewsController {
	
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    LikeService likeService;
    
    @Autowired
    CommentService commentService;

    @Autowired
    SensitiveService sensitiveService;
    
    @Autowired
    HostHolder hostHolder;
    
    /**
     * 添加新闻
     */
    @RequestMapping(path = {"/user/addNews"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image, 
    						@RequestParam("title") String title, 
    						@RequestParam("link") String link) {
    	try {
    		//防止xss 攻击，用 HtmlUtils.htmlEscape 将字符转义
    		News news = new News();
    		news.setTitle(sensitiveService.filter(HtmlUtils.htmlEscape(title)));
			news.setImage(image);
			news.setCreatedDate(new Date());
			news.setLink(sensitiveService.filter(HtmlUtils.htmlEscape(link)));
			
			if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 设置一个匿名用户
                news.setUserId(0);
            }
			
			newsService.setNewsScore(news);
			newsService.addNews(news);
			return ToutiaoUtil.getJSONString(0, "上传新闻成功");
		} catch (Exception e) {
			logger.error("上传资讯错误" + e.getMessage());
			return ToutiaoUtil.getJSONString(1, "上传新闻失败");
		}
    }

    /**
     * 添加评论
     */
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId, 
    							@RequestParam("content") String content) {
    	try {
    		Comment comment = new Comment();
    		comment.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(content)));
    		comment.setUserId(hostHolder.getUser().getId());
    		comment.setCreatedDate(new Date());
    		comment.setEntityId(newsId);
    		comment.setEntityType(EntityType.NEWS);
    		
    		commentService.addComment(comment);
    		
    		//更新评论数量
    		int count = commentService.SelectCount(newsId, EntityType.NEWS);
    		newsService.updateCommentCount(comment.getEntityId(), count);
    	} catch(Exception e) {
    		logger.error("提交评论错误 " + e.getMessage());
    	}
    	return "redirect:/news/" + String.valueOf(newsId);
    }
    
    /**
     * 新闻详情
     */
    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
    	try {
			News news = newsService.getById(newsId);
			 if (news != null) {
	                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.NEWS);
	                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
					for (Comment comment : comments) {
	                    ViewObject commentVO = new ViewObject();
	                    commentVO.set("comment", comment);
	                    commentVO.set("user", userService.getUser(comment.getUserId()));
	                    commentVOs.add(commentVO);
	                }
					model.addAttribute("comments", commentVOs);
            }
						
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
            
            if(hostHolder.getUser() == null) {
            	model.addAttribute("like", 0);
				return "detail";
			}
			
			int localUserId = hostHolder.getUser().getId();
            if (localUserId != 0) {
				model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.NEWS, news.getId()));
			} else {
				model.addAttribute("like", 0);
			}
		} catch (Exception e) {
			logger.error("获取资讯明细错误" + e.getMessage());
		}
    	
    	return "detail";
    }
    		
    /**
     * 获取图片
     */
    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response) {
    	try{
    		response.setContentType("image/png");
    		StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)), 
    				response.getOutputStream());
    	} catch(Exception e) {
    		logger.error("读取图片错误: " + imageName + e.getMessage());
    	}
    }
    
    /**
     * 删除新闻
     */ 
    @RequestMapping(path = {"/news/{newsId}/delete"}, method = {RequestMethod.GET})
    public String deleteNews(@PathVariable("newsId") int newsId) {
    	try{
			if(hostHolder.isAdmin()) {
				newsService.deleteNews(newsId);
				newsService.deleteNewsScore(newsId);
			} 
			
    	} catch(Exception e) {
    		logger.error("删除新闻错误: " + e.getMessage());
    	}
    	return "redirect:/";
    }
}
