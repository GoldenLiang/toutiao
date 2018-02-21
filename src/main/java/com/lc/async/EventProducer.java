package com.lc.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lc.util.JedisAdapter;
import com.lc.util.RedisUtil;

@Service
public class EventProducer {

	@Autowired
	JedisAdapter jedisAdapter;
	
	public boolean fireEvent(EventModel model) {
		try {
			String json = JSONObject.toJSONString(model);
			String key = RedisUtil.getQueueKey();
			jedisAdapter.lpush(key, json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
