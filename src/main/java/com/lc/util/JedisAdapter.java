package com.lc.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.lc.controller.LoginController;
import com.lc.crawler.IPModel.IPMessage;
import com.lc.crawler.IPModel.SerializeUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	private JedisPool pool = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		pool = new JedisPool("localhost", 6379);
	}
	
	/**
	 * 添加点赞
	 */
	public long sadd(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return 0;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 * 移除点赞
	 */
	public long srem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.srem(key, value);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return 0;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 * 是否点过赞
	 */
	public boolean sismember(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sismember(key, value);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return false;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	/**
	 * 点赞人数
	 */
	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return 0;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public void setObject(String key, Object obj) {
		set(key, JSON.toJSONString(obj));
	}
	
	public void set(String key, String value) {
		Jedis jedis = pool.getResource();
		jedis.set(key, value);
	}

	public void setExpireObject(String key, Object obj, int seconds) {
		Jedis jedis = pool.getResource();
		jedis.set(key, JSON.toJSONString(obj));
		jedis.expire(key, seconds);
	}
	
	public <T> T getObject(String key, Class<T> clazz) {
		String value = get(key);
		if(value != null) {
			return JSON.parseObject(value, clazz);
		}
		return null;
	}

	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
            if (jedis != null) {
                jedis.close();
            }
        }
		return null;
	}

	public void lpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.lpush(key, value);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
            if (jedis != null) {
                jedis.close();
            }
        }
	}

	public List<String> brpop(int i, String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.brpop(i, key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
            if (jedis != null) {
                jedis.close();
            }
        }
	}
	
	public String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.rpop(key);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		} finally {
            if (jedis != null) {
                jedis.close();
            }
        }
	}
	
	/**
	 * 是否关注
	 */
	public Double zscore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zscore(key, member);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return null;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public long zadd(String key, double score, String name) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zadd(key, score, name);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return 0;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public long zrem(String key, String name) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrem(key, name);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return 0;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zcard(key);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());
			return 0;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	
	public Set<String> zrange(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrange(key, start, end);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());		
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		return null;
	}
	
	public Set<String> zrevrange(String key, int start, int end) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());		
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		return null;
	}
	
	public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    // ..
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Long llen(String key) {
    	Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.llen(key);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());		
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		return null;
	}
    
    /**
     * 将ip信息保存在Redis列表中
     */
    public void setIPToList(List<IPMessage> ipMessages) {
    	Jedis jedis = null;
		try {
			jedis = pool.getResource();
	        for (IPMessage ipMessage : ipMessages) {
	            //首先将ipMessage进行序列化
	            byte[] bytes = SerializeUtil.serialize(ipMessage);
	
	            jedis.rpush("IPPool".getBytes(), bytes);
	        }
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());		
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
    }

    /**
     * 将Redis中保存的对象进行反序列化
     */
    public List<IPMessage> getIPByList() {
        List<IPMessage> IPPool = new ArrayList<>();
        Jedis jedis = null;
		try {
			jedis = pool.getResource();
	        for(int i = 0 ; i < 7; i++) {
	        	Object o = SerializeUtil.unserialize(jedis.lindex("IPPool".getBytes(), i));
		        if (o instanceof IPMessage) {
		        	IPPool.add((IPMessage)o);
		        } else {
		            logger.info("不是IPMessage的一个实例~");
		            continue;
		        }
	        }
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());		
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
        return IPPool;
    }

    public void deleteKey(String key) {
    	Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key);
		} catch (Exception e) {
			logger.error("发生异常" + e.getMessage());		
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		return ;
    }
    
	public Jedis getJedis() {
        return pool.getResource();
    }
}
