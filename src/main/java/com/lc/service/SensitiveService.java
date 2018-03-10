package com.lc.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	
	/**
	 * 前缀树根节点
	 */
	private TrieNode rootNode = new TrieNode();
			
	/**
	 * @author lc
	 *
	 */
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
		 * fail指针
		 */
		private TrieNode failure;
		
		private TrieNode parent; 
		
		private char ch;  
        
        private ArrayList<String> results;  
                    
        private TrieNode[] sonsNode;  
        
        public TrieNode() {
        	
        }
		
		public TrieNode(char ch, TrieNode parent) {  
            this.parent = parent;  
            this.ch = ch;  
            results = new ArrayList<String>(); 
            subNodes = new HashMap<>();
            sonsNode = new TrieNode[]{};  
        }  
		
		//获取字符  
        public char getChar(){  
            return ch;  
        }  
          
        //获取父节点  
        public TrieNode getParent(){  
            return parent;  
        }  
        //设置失败指针并且返回  
        public TrieNode setFailure(TrieNode failure){  
            this.failure = failure;  
            return this.failure;  
        }  
          
        //获取所有的孩子节点  
        public TrieNode[] getSonsNode(){  
            return sonsNode;  
        }  
        //获取搜索的字符串  
        public ArrayList<String> getResults(){  
            return results;  
        }
        
        //判断子节点中是否存在该字符  
        public boolean containNode(char ch){  
            return getSubNodes(ch) !=null;  
        }  
          
        //添加一个结果到结果字符中  
        public void addResult(String result){  
            if(!results.contains(result)) results.add(result);  
        }
        
        //添加子节点  
        public void addSonNode(TrieNode node){  
            subNodes.put(node.ch, node);  
            sonsNode = new TrieNode[subNodes.size()];  
            Iterator<TrieNode> iterator = subNodes.values().iterator();  
            for (int i = 0; i < sonsNode.length; i++) {  
                if(iterator.hasNext()){  
                    sonsNode[i] = iterator.next();  
                }  
            }  
        }
        
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
		
		public int getSubNodesCount() {
			return subNodes.size();
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rootNode = new TrieNode(' ', null);
		
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("SensitiveWord.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String lineTxt;
			while((lineTxt = br.readLine()) != null) {
				lineTxt = lineTxt.trim();
				buildTree(lineTxt, rootNode);
			}
			
			addFailure();
			br.close();
		} catch (Exception e) {
			logger.error("读取文件敏感词出错" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加敏感词
	 * @param lineText
	 */
//	private void addKeyWord(String lineText) {
//		TrieNode tempNode = rootNode;
//		
//		for(int i = 0; i < lineText.length(); i++) {
//			Character c = lineText.charAt(i);
//			// 过滤空格
//            if (isSymbol(c)) {
//                continue;
//            }
//            TrieNode node = tempNode.getSubNodes(c);
//			
//			if(node == null) {
//				node = new TrieNode();
//				tempNode.addSubNodes(c, node);
//				tempNode.addSonNode(node);
//			}
//			
//			tempNode = node;
//			
//			if(i == lineText.length() - 1) {
//				tempNode.setKeyWordEnd(true);
//			}
//		}
//	}
	
	
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
//	public String filter(String text) {
//		StringBuffer result = new StringBuffer();
//		TrieNode node = rootNode;
//		
//		if(StringUtils.isBlank(text)) {
//			return text;
//		}
//		
//		int begin = 0;	//回滚数
//		int position = 0;	//当前位置
//		String replacement = "*河蟹*";
//		
//		while(position < text.length()) {
//			char c = text.charAt(position);
//				
//			if(isSymbol(c)) {
//				if(node == rootNode) {
//					result.append(c);
//					begin++;
//				}
//				position++;
//				continue;
//			}
//			
//			node = node.getSubNodes(c);
//			
//			if(node == null) {
//				// 以begin开始的字符串不存在敏感词
//                result.append(text.charAt(begin));
//				position = begin + 1;
//				begin = position;
//				// 回到树初始节点
//                node = rootNode;
//			} else if(node.isKeyWordEnd()) {
//				result.append(replacement);
//				position++;
//				begin = position;
//				node = rootNode;
//			} else {
//				position++;
//			}
//		}
//		
//		result.append(text.substring(begin));
//		return result.toString();
//	}
	
	//查找全部的模式串  
    public String filter(String text){  
        //可以找到 转移到下个节点 不能找到在失败指针节点中查找直到为root节点  
    	StringBuffer result = new StringBuffer(); 
        int index = 0;  
        int position = 0;
        TrieNode mid = rootNode;  
        String replacement = "*河蟹*";
        while(position < text.length()){  
        	if(StringUtils.isBlank(text)) {
    			return text;
    		}
        	
        	char c = text.charAt(position);
        	
        	if(isSymbol(c)) {
				if(mid == rootNode) {
					result.append(c);
					
				}
				position++;
				continue;
			}
        	
        	TrieNode temp = null;  
            boolean only = true;
            
            while(temp == null){  
                temp = mid.getSubNodes(text.charAt(position));  
                
                if(temp == null && only) {  
                	result.append(text.charAt(index));
                	position = index + 1;
                	index = position;
                	only = false;
                }
                if(mid == rootNode) {  
                    break;  
                }  
                mid = mid.failure;
            }  
            

            //mid为root 再次进入循环 不需要处理  或者 temp不为空找到节点 节点位移  
            if(temp != null) {
            	mid = temp;  
            	if(temp.isKeyWordEnd()) {
            		result.append(replacement);
            		position++;
    				index = position;
            	} else {
            		position++;
            	}
            }
                          
        }  
        return result.toString();  
    }
    
	private void addFailure() {  
        //设置二层失败指针为根节点 收集三层节点  
          
        //DFA遍历所有节点 设置失败节点 原则: 节点的失败指针在父节点的失败指针的子节点中查找 最大后缀匹配   
        ArrayList<TrieNode> mid = new ArrayList<TrieNode>();//过程容器  
        for (TrieNode node : rootNode.getSonsNode()) {  
            node.failure = rootNode;  
            for (TrieNode treeNode : node.getSonsNode()) {  
                mid.add(treeNode);  
            }  
        }  
          
        //广度遍历所有节点设置失败指针 1.存在失败指针 2.不存在到root结束  
        while(mid.size()>0){  
            ArrayList<TrieNode> temp = new ArrayList<TrieNode>();//子节点收集器  
              
            for (TrieNode node : mid) {  
                  
            	TrieNode r = node.getParent().failure;  
                  
                while(r!=null && !r.containNode(node.getChar())){  
                    r = r.failure;//没有找到,保证最大后缀 (最后一个节点字符相同)  
                }  
                  
                //是根结  
                if(r==null){  
                    node.failure = rootNode;  
                }else{  
                    node.failure = r.getSubNodes(node.getChar());  
                    //重叠后缀的包含  
                    for (String result : node.failure.getResults()) {  
                        node.addResult(result);  
                    }  
                }  
                  
                //收集子节点  
                for (TrieNode treeNode : node.getSonsNode()) {  
                    temp.add(treeNode);  
                }  
                  
            }  
            mid = temp;  
        }  
        rootNode.failure = rootNode;  
    }  
  
    private void buildTree(String keyword, TrieNode rootNode) {  
    	
    	
    	
    	//for (String keyword : keywords) {
    		TrieNode temp = rootNode;
	        //判断节点是否存在 存在转移 不存在添加  
	    	for(int i = 0; i < keyword.length(); i++) {
				Character ch = keyword.charAt(i);  
	        	// 过滤空格
	            if(isSymbol(ch)) {
	                continue;
	            }
	            
	            if(temp.containNode(ch)) {  
	                temp = temp.getSubNodes(ch);  
	            } else {  
	            	TrieNode newNode = new TrieNode(ch, temp);  
	                temp.addSonNode(newNode);  
	                temp = newNode;  
	            }   
	            
	            if(i == keyword.length() - 1) {
	            	temp.setKeyWordEnd(true);
	            }
	        }  
	    	
	        temp.addResult(keyword);  
    	//}
    } 
    
}
