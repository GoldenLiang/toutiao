package com.lc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lc.util.JedisAdapter;
import com.lc.util.RedisUtil;

import redis.clients.jedis.Transaction;

@Service
public class FollowService {

	@Autowired
	JedisAdapter jedisAdapter;

	/**
	 * 关注
	 * @param userId 关注者
	 * @param entityType 被关注的类型
	 * @param entityId	被关注者
	 * @return
	 */
	public boolean follow(int userId, int entityType, int entityId) {
		String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
		String followeeKey = RedisUtil.getFolloweeKey(entityId, entityType);
		Date  date = new Date();
		Transaction tx = jedisAdapter.multi(jedisAdapter.getJedis());
		tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
		tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
		List<Object> result = jedisAdapter.exec(tx,jedisAdapter.getJedis());
		if(result == null) {
			return false;
		}
		return result.size() == 2 && (Long) result.get(0) > 0 && (Long) result.get(1) > 0;
	}
	
	/**
	 * 取消关注
	 * @param userId 关注者
	 * @param entityType 被关注的类型
	 * @param entityId	被关注者
	 * @return
	 */
	public boolean unfollow(int userId, int entityType, int entityId) {
		String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
		String followeeKey = RedisUtil.getFolloweeKey(entityId, entityType);
		Transaction tx = jedisAdapter.multi(jedisAdapter.getJedis());
		tx.zrem(followerKey, String.valueOf(userId));
		tx.zrem(followeeKey, String.valueOf(entityId));
		List<Object> result = jedisAdapter.exec(tx, jedisAdapter.getJedis());
		if(result == null) {
			return false;
		}
		return result.size() == 2 && (Long) result.get(0) > 0 && (Long) result.get(1) > 0;
	}
	
	private List<Integer> getIdsFromSet(Set<String> set) {
		List<Integer> ids = new ArrayList<>();
		for(String str : set) {
			ids.add(Integer.parseInt(str));
		}
		return ids;
	}
	
	/**
	 * 获取粉丝
	 */
	public List<Integer> getFollowers(int entityId, int entityType, int count) {
		String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
	}
	
	/**
	 * 获取关注的人
	 */
	public List<Integer> getFollowees(int entityId, int entityType, int offset, int count) {
		String followeeKey = RedisUtil.getFollowerKey(entityId, entityType);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
	}
	
	/**
	 * 粉丝人数
	 */
	public long getFollowersCount(int entityId, int entityType) {
		String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
		return jedisAdapter.zcard(followerKey);
	}
	
	/**
	 * 关注人人数
	 */
	public long getFolloweesCount(int entityId, int entityType) {
		String followeeKey = RedisUtil.getFollowerKey(entityId, entityType);
		return jedisAdapter.zcard(followeeKey);
	}
	
	/**
	 * 是否关注
	 */
	public boolean isFollower(int userId, int entityId, int entityType) {
		String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
		return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
	}
}
