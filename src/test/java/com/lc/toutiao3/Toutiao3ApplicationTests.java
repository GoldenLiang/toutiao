package com.lc.toutiao3;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lc.ToutiaoApplication;
import com.lc.model.News;
import com.lc.service.NewsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class Toutiao3ApplicationTests {

	@Autowired
	NewsService newsService;
	
	@Test
	public void contextLoads() {
		List<News> newslist =  newsService.getLastestNews(0, 0, 10);
		for(News news : newslist) {
			System.out.println(news);
		}
	}

}
