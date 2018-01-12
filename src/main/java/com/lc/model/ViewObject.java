package com.lc.model;

import java.util.HashMap;

/**
 * 视图展示对象
 * @author lc
 */
public class ViewObject {
	
	HashMap<String, Object> map = new HashMap<>();
	
	public void set(String key, Object value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}
}
