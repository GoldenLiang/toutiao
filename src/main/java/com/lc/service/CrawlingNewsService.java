package com.lc.service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lc.dao.NewsDAO;
import com.lc.model.News;

/**
 * 爬取新闻
 */
@Service
@EnableScheduling  
public class CrawlingNewsService {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingNewsService.class);
	
	@Autowired
    private NewsDAO newsDAO;

	@Scheduled(fixedRate = 60000 * 30)   //每10分钟执行一次   
	public void saveCrawlingNews() {
		
		logger.info("开始爬取...");
		
		// 需要爬的网页的文章列表
		String url = "http://www.toutiao.com/news_tech/";
		// 文章详情页的前缀(由于今日头条的文章都是在group这个目录下,所以定义了前缀,而且通过请求获取到的html页面)
		String url2 = "www.toutiao.com/group/";
		// 链接到该网站
		Connection connection = Jsoup.connect(url);
		Document content = null;

		try {
			// 获取内容
			content = connection.get();
			// 转换成字符串
			String htmlStr = content.html();
			// 因为今日头条的文章展示比较奇葩,都是通过js定义成变量,所以无法使用获取dom元素的方式获取值
			String jsonStr = StringUtils.substringBetween(htmlStr, "var _data = ", ";");

			Map parse = (Map) JSONObject.parse(jsonStr);
			JSONArray parseArray = (JSONArray) parse.get("real_time_news");
			Map map = null;
			List<Map> maps = new ArrayList<>();

			// 遍历这个jsonArray,获取到每一个json对象,然后将其转换成Map对象(在这里其实只需要一个group_id,那么没必要使用map)
			for (int i = 0; i < parseArray.size(); i++) {
				News news = new News();
				Date date = new Date();
				map = (Map) parseArray.get(i);
				maps.add((Map) parseArray.get(i));

				news.setLink(url2 + map.get("group_id"));
				news.setTitle((String) map.get("title"));
				news.setImage((String) map.get("image_url"));
				news.setCreatedDate(date);
				news.setUserId(1);
				news.setCommentCount(0);
				news.setLikeCount(0);

				newsDAO.addNews(news);
			}
		} catch (IOException e) {
			logger.error("获取内容出错 ：" + e.getMessage());
		}

	}
}
