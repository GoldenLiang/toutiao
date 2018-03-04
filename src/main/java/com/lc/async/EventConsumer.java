package com.lc.async;

import com.alibaba.fastjson.JSON;
import com.lc.util.JedisAdapter;
import com.lc.util.RedisUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
	
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;
    
    @Autowired
    private JedisAdapter jedisAdapter;
 
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportTypes();
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }

                    // 注册每个事件的处理函数
                    config.get(type).add(entry.getValue());
                }
            }
        }

        // 启动线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 从队列一直消费
                while (true) {
                    String key = RedisUtil.getQueueKey();
	                    List<String> messages = jedisAdapter.brpop(0, key);
	                    // 第一个元素是队列名字
	                    for (String message : messages) {
	                        if (message.equals(key)) {
	                            continue;
	                        }
	
	                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
	//                        logger.error("beans == null ?" + beans.toString());
	//                        logger.info("containsKey ?" + config.containsKey(eventModel.getType()));
	                        // 找到这个事件的处理handler列表
	                        if (!config.containsKey(eventModel.getType())) {
	                            logger.error("不能识别的事件 :  " + eventModel.getType());
	                            continue;
	                        }
	                        
	                        EventHandler handler;
							for (int index = 0; index < config.get(eventModel.getType()).size(); index++) {
	                            handler = config.get(eventModel.getType()).get(index);
	                            handler.doHandle(eventModel);
	                            logger.info("handler:" + handler.toString()  + " 处理  " + eventModel.getType());
							}
	                        
	                    }
                	}
                }
            
        });
        thread.setName("consumer" + thread.getId());
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
