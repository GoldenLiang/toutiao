package com.lc.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 敏感词过滤
 */
@Service
public class SensitiveService implements InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
	
	private class TrieNode {
		/**
		 * 是否是敏感词结尾
		 */
		private boolean end = false;
		
		/**
		 * Key 是下一个字符， Value 是对应的节点
		 */
		private HashMap<Character, TrieNode> subNodes = new HashMap<>();
		
		/**
		 * 向指定位置添加节点树
		 * @param key
		 * @param value
		 */
		private void addSubNodes(Character key, TrieNode value) {
			subNodes.put(key, value);
		}
		
		/**
		 * 获取下个节点
		 * @param key
		 * @return
		 */
		public TrieNode getSubNodes(Character key) {
			return subNodes.get(key);
		}
		
		private boolean isKeyWordEnd() {
			return end;
		}
		
		private void setKeyWordEnd(boolean end) {
			this.end = end;
		}
		
		private int getSubNodesCount() {
			return subNodes.size();
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rootNode = new TrieNode();
		
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("SensitiveWord.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String lineTxt;
			while((lineTxt = br.readLine()) != null) {
				lineTxt = lineTxt.trim();
				addKeyWord(lineTxt);
			}
			
			br.close();
		} catch (Exception e) {
			logger.error("读取文件敏感词出错" + e.getMessage());
		}
	}
	
	/**
	 * 前缀树根节点
	 */
	private TrieNode rootNode = new TrieNode();
	
	/**
	 * 添加敏感词
	 * @param lineText
	 */
	private void addKeyWord(String lineText) {
		TrieNode tempNode = rootNode;
		
		for(int i = 0; i < lineText.length(); i++) {
			Character c = lineText.charAt(i);
			// 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNodes(c);
			
			if(node == null) {
				node = new TrieNode();
				tempNode.addSubNodes(c, node);
			}
			
			tempNode = node;
			
			if(i == lineText.length() - 1) {
				tempNode.setKeyWordEnd(true);
			}
		}
	}
	
	
	/**
	 * 判断是不是非法字符
	 * @return
	 */
	private boolean isSymbol(char c) {
		int ic = (int) c;
		// 0x2E80-0x9FFF 东亚文字范围
		return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2e80 || ic > 0x9fff);
	}
	
	/**
	 * 过滤敏感词
	 * @param text 过滤文本
	 * @return
	 */
	public String filter(String text) {
		StringBuffer result = new StringBuffer();
		TrieNode node = rootNode;
		
		if(StringUtils.isBlank(text)) {
			return text;
		}
		
		int begin = 0;	//回滚数
		int position = 0;	//当前位置
		String replacement = "*河蟹*";
		
		while(position < text.length()) {
			char c = text.charAt(position);
				
			if(isSymbol(c)) {
				if(node == rootNode) {
					result.append(c);
					begin++;
				}
				position++;
				continue;
			}
			
			node = node.getSubNodes(c);
			
			if(node == null) {
				// 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
				position = begin + 1;
				begin = position;
				// 回到树初始节点
                node = rootNode;
			} else if(node.isKeyWordEnd()) {
				result.append(replacement);
				position++;
				begin = position;
				node = rootNode;
			} else {
				position++;
			}
		}
		
		result.append(text.substring(begin));
		return result.toString();
	}
	
}
