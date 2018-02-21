package com.lc.util;

public class RedisUtil {

	private static String SPLIT = ":";
	private static String BIZ_LIKE = "LIKE";
	private static String BIZ_DISLIKE = "DISLIKE";
	private static String BIZ_EVENT = "EVENT";
	private static String BIZ_FOLLOWER = "FOLLOWER";
	private static String BIZ_FOLLOWEE = "FOLLOWEE";
	
	public static String getQueueKey() {
		return BIZ_EVENT;
	}
	
	public static String getLikeKey(int entityId, int entityType) {
		return BIZ_LIKE + SPLIT + entityType + SPLIT + String.valueOf(entityId);
	}

	public static String getDisLikeKey(int entityId, int entityType) {
		return BIZ_DISLIKE + SPLIT + entityType + SPLIT + String.valueOf(entityId);
	}
	
	public static String getFollowerKey(int entityId, int entityType) {
		return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + String.valueOf(entityId);
	}
	
	public static String getFolloweeKey(int entityId, int entityType) {
		return BIZ_FOLLOWEE + SPLIT + entityType + SPLIT + String.valueOf(entityId);
	}
}
