package com.lc.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lc.dao.NewsDAO;
import com.lc.model.News;
import com.lc.service.NewsService;

@Service
public class NewsServiceImpl implements NewsService {
	
	@Autowired
	NewsDAO newsDAO;

	@Override
	public List<News> getLastestNews(int id, int offset, int limit) {
		return newsDAO.getLastestNews(id, offset, limit);
	}
	   
}
