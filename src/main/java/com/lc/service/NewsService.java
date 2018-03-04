package com.lc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lc.dao.NewsDAO;
import com.lc.model.News;
import com.lc.util.JedisAdapter;
import com.lc.util.ToutiaoUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;
    
    @Autowired
	JedisAdapter jedisAdapter;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
    
    public int getNewsCount(int userId) {
    	return newsDAO.selectCount(userId);
    }
    
    public synchronized List<News> setAllNewsScore(List<News> newsList) { 
    	List<News> resList =  JSONObject.parseArray(jedisAdapter.get("newsListCache"), News.class);
    	if(resList == null) {
	    	for(News news : newsList) {
		    	double score = rankingNewsAlgorithm(news);
		    	jedisAdapter.zadd("newsList", score, String.valueOf(news.getId()));
	    	}
	    	resList =  getNewsFromSet(jedisAdapter.zrevrange("newsList", 0, 9));
	    	jedisAdapter.setExpireObject("newsListCache", resList, 60);
    	}
	    return resList;
    }
    
    public void setNewsScore(News news) {  
    	double score = rankingNewsAlgorithm(news);
    	jedisAdapter.zadd("newsList", score, String.valueOf(news.getId()));
    }
    
    public void deleteNewsScore(int newsId) {  
    	jedisAdapter.zrem("newsList", String.valueOf(newsId));
    }
	
    public double rankingNewsAlgorithm(News news) {
    	Date date = new Date();
	    double G = 1.8;
	    double T = date.getTime() - news.getCreatedDate().getTime();
		double score = (news.getLikeCount() - 1) * Math.pow((T + 2), G);
		return score;
    }
    
    private List<News> getNewsFromSet(Set<String> set) {
		List<News> news = new ArrayList<>();
		for(String str : set) {
			news.add(getById(Integer.parseInt(str)));
		}
		return news;
	}
//   public synchronized List<News> rankingNews(List<News> newsList) {  
//    	List<News> resList =  JSONObject.parseArray(jedisAdapter.get("newsList"), News.class);
//    	if(resList == null) {
//	    	Collections.sort(newsList, new Comparator<News>() {
//				@Override
//				public int compare(News n1, News n2) {
//					Date date = new Date();
//					long T1 = date.getTime() - n1.getCreatedDate().getTime();
//					long T2 = date.getTime() - n2.getCreatedDate().getTime();
//					double G = 1.8;
//					Double d1 = (n1.getLikeCount() - 1) * Math.pow((T1 + 2), G);
//					Double d2 = (n2.getLikeCount() - 1) * Math.pow((T2 + 2), G);
//					return d2.compareTo(d1);  
//				}
//	
//	    	});
//	    	resList = newsList.subList(0, 10);
//	    	jedisAdapter.setExpireObject("newsList", resList, 60);
//    	}
//    	return resList;
//    }
    
    public String saveImage(MultipartFile file) throws IOException {
    	//xx.jpg
    	int dotPos = file.getOriginalFilename().lastIndexOf(".");
    	if(dotPos < 0) {
    		return null;
    	}
    	
    	//获取后缀并检查是否为图片后缀
    	String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
    	if(!ToutiaoUtil.isFileAllowed(fileExt)) {
    		return null;
    	}
    	
    	//为图片添加文件名
    	String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
    	//上传到指定地址
    	Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + filename).toPath(),
    			StandardCopyOption.REPLACE_EXISTING);
    	
    	return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + filename;
    }

	public News getById(int newsId) {
		return newsDAO.getById(newsId);
	}

	public int addNews(News news) {
		return newsDAO.addNews(news);
	}

	public long updateLikeCount(int newsId, int likeCount) {
		return newsDAO.updateLikeCount(newsId, likeCount);
	}

	public int updateCommentCount(int id, int count) {
		return newsDAO.updateCommentCount(id, count);
	}

	public String getNewsTitle(int entityId) {
		return newsDAO.getNewsTitle(entityId);
	}

	public List<News> getLatestNews(String date) {
		return newsDAO.getLatestNews(date);
	}

	public int deleteNews(int newsId) {
		return newsDAO.deleteNews(newsId);
	}
	
}
