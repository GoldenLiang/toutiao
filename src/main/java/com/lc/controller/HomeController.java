package com.lc.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lc.model.News;
import com.lc.model.ViewObject;
import com.lc.service.NewsService;

@Controller
public class HomeController {

	@Autowired
	NewsService newsService;
	
	@RequestMapping("/news")
	public String news() {
		return "news";
	}
	
	@RequestMapping(path={"/", "/index"}, method={RequestMethod.GET, RequestMethod.POST})
	public String header(Model model) {
		model.addAttribute("vo", getNews(0, 0, 10));
		return "header";
	}

	private List<ViewObject> getNews(int id, int offset, int limit) {
		List<News> newslist =  newsService.getLastestNews(id, offset, limit);
		List<ViewObject> vos = new ArrayList<>();
		
		for(News news : newslist) {
			ViewObject vo = new ViewObject();
			vo.set("news", news);
			
			vos.add(vo);
		}
		
		return vos;
	}
}
