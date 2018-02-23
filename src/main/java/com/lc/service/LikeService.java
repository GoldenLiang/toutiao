package com.lc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lc.util.JedisAdapter;
import com.lc.util.RedisUtil;

@Service
public class LikeService {

	@Autowired
	JedisAdapter jedisAdapter;
	
	@Autowired
    NewsService newsService;
	
	/**
	 * 喜欢返回1，不喜欢返回-1，否则返回0
	 * @param userId
	 */
	public int getLikeStatus(int userId, int entityType, int entityId) {
		String likeKey = RedisUtil.getLikeKey(entityId, entityType);
		if(jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
			return 1;
		}
		
		String dislikeKey = RedisUtil.getDisLikeKey(entityId, entityType);
		if(jedisAdapter.sismember(dislikeKey, String.valueOf(userId))) {
			return -1;
		}
		
		return 0;
	} 
	
	/**
	 * 喜欢某个评论、新闻
	 */
	public long like(int userId, int entityType, int entityId) {
		String likeKey = RedisUtil.getLikeKey(entityId, entityType);
		jedisAdapter.sadd(likeKey, String.valueOf(userId));
		
		String dislikeKey = RedisUtil.getDisLikeKey(entityId, entityType);
		jedisAdapter.srem(dislikeKey, String.valueOf(userId));
		
		newsService.setNewsScore(newsService.getById(entityId));

		return jedisAdapter.scard(likeKey);
	}
	
	/**
	 * 不喜欢某个评论、新闻
	 */
	public long dislike(int userId, int entityType, int entityId) {
	 	String likeKey = RedisUtil.getLikeKey(entityId, entityType);
		jedisAdapter.srem(likeKey, String.valueOf(userId));
		
		String dislikeKey = RedisUtil.getDisLikeKey(entityId, entityType);
		jedisAdapter.sadd(dislikeKey, String.valueOf(userId));
		
		newsService.setNewsScore(newsService.getById(entityId));
		
		return jedisAdapter.scard(likeKey);
	}
	
	/**
	 * 返回喜欢的人数
	 */
	public long getLikeCount(int userId, int entityType, int entityId) {
		String likeKey = RedisUtil.getLikeKey(entityId, entityType);
		return jedisAdapter.scard(likeKey); 
	}
}
