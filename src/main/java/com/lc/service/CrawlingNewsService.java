package com.lc.service;

import static java.lang.System.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.lc.crawler.IPModel.IPMessage;
import com.lc.crawler.htmlparse.IPPool;
import com.lc.crawler.htmlparse.IPThread;
import com.lc.crawler.htmlparse.URLFecter;
import com.lc.crawler.ipfilter.IPFilter;
import com.lc.crawler.ipfilter.IPUtils;
import com.lc.dao.NewsDAO;
import com.lc.model.News;
import com.lc.util.JedisAdapter;

/**
 * 爬取新闻
 */
@Service
@EnableScheduling  
public class CrawlingNewsService {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingNewsService.class);
	
	@Autowired
    private NewsDAO newsDAO;
	
	@Autowired
	JedisAdapter jedisAdapter;
	
	ExecutorService threadPool = Executors. newFixedThreadPool(4);

	@Scheduled(fixedRate = 60000 * 30)   //每30分钟执行一次   
	public void saveCrawlingNews() {
		
		logger.info("开始爬取...");		
		
		threadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				String url = "https://www.toutiao.com/";
				Runtime rt = Runtime.getRuntime();
				String exec = "E://phantomjs-2.1.1-windows/bin/phantomjs E://phantomjs-2.1.1-windows/code.js " + url;
				Process process = null;
				PrintWriter out = null;
				try {
					process = rt.exec(exec);
					InputStream is = process.getInputStream();
					Document doc = Jsoup.parse(is, "UTF-8", url);
					String html = doc.html();
				    System.out.println("=============写入中...");
					out = new PrintWriter(new BufferedWriter(new FileWriter("news.txt")));
					out.write(html);
				} catch (IOException e) {
					e.printStackTrace();
				}  finally {
					if(out != null) 
						out.close();
				}
				File input = new File("news.txt");
				Document doc = null;
				try {
					doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
				} catch (IOException e) {
					e.printStackTrace();
				}

				Elements links = doc.select("a[href]"); //带有href属性的a元素
				Elements hotTitle = doc.select("p.module-title"); //带有module-title属性的p元素
				Elements title = doc.select("p.title"); //带有title属性的p元素
				Elements pngs = doc.select("img[src]");//扩展名为.png的图片
				Elements resultLinks = doc.select("li.bui-left");//带有bui-left属性的li元素
				
//				System.out.println("==========================hotTitle==========================l");
//				System.out.println(hotTitle.html());
//				System.out.println("==========================title==========================l");
//				System.out.println(title.html());
				String[] hotNews = title.html().split("\n");
				String[] myNews = hotTitle.html().split("\n");
				int newsLen = hotNews.length + myNews.length;
				
//				System.out.println("=============resultLinks");
				String p = resultLinks.html();
				List<String> news2 = new ArrayList<>();
				while(p != null) {
					int index = p.indexOf("</p>");
					if(index == -1) break;
					news2.add(StringUtils.substringBetween(p, "<p>", "</p>"));
					p = p.substring(index + 1);
				}	
				
//				System.out.println("=============links");
				String[] newsLinks = new String[newsLen + news2.size()];
				int count = 0;
				for(Element src : links) {
					if(src.attr("href").startsWith("/g"))
						newsLinks[count++] = src.attr("href");
				}
				
//				System.out.println("=============pngs");
				String[] imageLinks = new String[newsLen + news2.size()];
				int countImage = 0;
				for(Element src : pngs) {
					if(!src.attr("src").startsWith("//s"))
						imageLinks[countImage++] = src.attr("src");
				}
				
				count = 0;
				String toutiaoUrl = "www.toutiao.com";
				for (int i = 0; i < newsLen + news2.size(); i++) {
					News news = new News();
					Date date = new Date();
					
					news.setCreatedDate(date);
					news.setUserId(1);
					news.setCommentCount(0);
					news.setLikeCount(0);
					news.setImage(imageLinks[i]);
					news.setLink(toutiaoUrl + newsLinks[i]);
					
					if(i < hotNews.length) {
						news.setTitle(hotNews[i]);
					} else if(i < newsLen) {
						news.setTitle(myNews[i - hotNews.length]);
					} else if(i >= newsLen) {
						news.setTitle(news2.get(count++));
					}
					
					newsDAO.addNews(news);
				}
				
			}
		});

	}
	
//	@Scheduled(fixedRate = 24 * 60 * 60 * 1000)   //每24小时执行一次   
//	public void IPPool() {
//		threadPool.execute(new Runnable() {
//	
//			@Override
//		    public void run() {
//		        //首先清空redis数据库中的key
//		        jedisAdapter.deleteKey("IPPool");
//	
//		        //存放爬取下来的ip信息
//		        List<IPMessage> ipMessages = new ArrayList<>();
//		        List<String> urls = new ArrayList<>();
//		        //对创建的子线程进行收集
//		        List<Thread> threads = new ArrayList<>();
//	
//		        //首先使用本机ip爬取xici代理网第一页
//		        ipMessages = URLFecter.urlParse(ipMessages);
//	
//		        //对得到的IP进行筛选，将IP速度在两秒以内的并且类型是https的留下，其余删除
//		        ipMessages = IPFilter.Filter(ipMessages);
//	
//		        //对拿到的ip进行质量检测，将质量不合格的ip在List里进行删除
//		        IPUtils.IPIsable(ipMessages);
//	
//		        //构造种子url(4000条ip)
//		        for (int i = 2; i <= 41; i++) {
//		            urls.add("http://www.xicidaili.com/nn/" + i);
//		        }
//	
//		        /**
//		         * 对urls进行解析并进行过滤,拿到所有目标IP(使用多线程)
//		         *
//		         * 基本思路是给每个线程分配自己的任务，在这个过程中List<IPMessage> ipMessages
//		         * 应该是共享变量，每个线程更新其中数据的时候应该注意线程安全
//		         */
//		        IPPool ipPool = new IPPool(ipMessages);
//		        for (int i = 0; i < 20; i++) {
//		            //给每个线程进行任务的分配
//		            Thread IPThread = new IPThread(urls.subList(i*2, i*2+2), ipPool);
//		            threads.add(IPThread);
//		            IPThread.start();
//		        }
//	
//		        for (Thread thread : threads) {
//		            try {
//		                thread.join();
//		            } catch (InterruptedException e) {
//		                e.printStackTrace();
//		            }
//		        }
//	
//		        //将爬取下来的ip信息写进Redis数据库中(List集合)
//		        jedisAdapter.setIPToList(ipMessages);
//	
//		    }
//		
//		});
//	}
}
