package com.lc.service;

import java.util.List;

import com.lc.model.News;

public interface NewsService {

	List<News> getLastestNews(int id, int offset, int limit);

}
