package cn.edu.bjtu.weibo.service.impl;

//package cn.edu.bjtu.weibo.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FilterBanedKeywordsServiceImpl implements FilterBanedKeywordsService{
	@SuppressWarnings("rawtypes")
	private Map sensitiveWordMap = null;
	public static int minMatchTYpe = 1;      //最小匹配规则
	public static int maxMatchType = 2;      //最大匹配规则
	
	@Override
	
	
	public boolean isBanedKeywordInside(String weiboContent) {
		// TODO Auto-generated method stub
		boolean flag = false;
		BanedKeywordServiceImpl keyWord = new BanedKeywordServiceImpl();
		
		Set<String> keyWordSet = new HashSet();
		keyWordSet.addAll(keyWord.getAllWord());
		
		sensitiveWordMap = addSensitiveWordToHashMap(keyWordSet);
		for(int i = 0 ; i < weiboContent.length() ; i++){
			int matchFlag = this.CheckSensitiveWord(weiboContent, i, 1); //判断是否包含敏感字符
			if(matchFlag > 0){    //大于0存在，返回true
				flag = true;
			}
		}
		return flag;
		 
	}

	 private Map addSensitiveWordToHashMap(Set<String> keyWordSet) {  
			Map sensitiveWordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作  
	        String key = null;    
	        Map nowMap = null;  
	        Map<String, String> newWorMap = null;  
	        //迭代keyWordSet  
	        Iterator<String> iterator = keyWordSet.iterator();  
	        while(iterator.hasNext()){  
	            key = iterator.next();    //关键字  
	            nowMap = sensitiveWordMap;  
	            for(int i = 0 ; i < key.length() ; i++){  
	                char keyChar = key.charAt(i);       //转换成char型  
	                Object wordMap = nowMap.get(keyChar);       //获取  
	                  
	                if(wordMap != null){        //如果存在该key，直接赋值  
	                    nowMap = (Map) wordMap;  
	                }  
	                else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个  
	                    newWorMap = new HashMap<String,String>();  
	                    newWorMap.put("isEnd", "0");     //不是最后一个  
	                    nowMap.put(keyChar, newWorMap);  
	                    nowMap = newWorMap;  
	                }
	                if(i == key.length() - 1){  
	                    nowMap.put("isEnd", "1");    //最后一个  
	                }  
	            }  
	        } 
	        return sensitiveWordMap;
	    }
	 
	 @SuppressWarnings({ "rawtypes"})
		public int CheckSensitiveWord(String txt,int beginIndex,int matchType){
			boolean  flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
			int matchFlag = 0;     //匹配标识数默认为0
			char word = 0;
			Map nowMap = sensitiveWordMap;
			for(int i = beginIndex; i < txt.length() ; i++){
				word = txt.charAt(i);
				nowMap = (Map) nowMap.get(word);     //获取指定key
				if(nowMap != null){     //存在，则判断是否为最后一个
					matchFlag++;     //找到相应key，匹配标识+1 
					if("1".equals(nowMap.get("isEnd"))){       //如果为最后一个匹配规则,结束循环，返回匹配标识数
						flag = true;       //结束标志位为true   
						if(FilterBanedKeywordsServiceImpl.minMatchTYpe == matchType){    //最小规则，直接返回,最大规则还需继续查找
							break;
						}
					}
				}
				else{     //不存在，直接返回
					break;
				}
			}
			if(matchFlag < 2 || !flag){        //长度必须大于等于1，为词 
				matchFlag = 0;
			}
			return matchFlag;
		}
}
