package cn.edu.bjtu.weibo.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FilterBanedKeywordsServiceImpl implements FilterBanedKeywordsService{
	@SuppressWarnings("rawtypes")
	private Map sensitiveWordMap = null;
	public static int minMatchType = 1;      //Minimum matching Type
	public static int maxMatchType = 2;      //Maximum matching Type
	
	@Override
	
	
	public boolean isBanedKeywordInside(String weiboContent) {
		// TODO Auto-generated method stub
		boolean flag = false;
		BanedKeywordServiceImpl keyWord = new BanedKeywordServiceImpl();
		
		Set<String> keyWordSet = new HashSet();
		keyWordSet.addAll(keyWord.getAllWord());
		keyWordSet.add("你好");
		
		sensitiveWordMap = addSensitiveWordToHashMap(keyWordSet);
		for(int i = 0 ; i < weiboContent.length() ; i++){
			int matchFlag = this.CheckSensitiveWord(weiboContent, i, 1); //Determines if sensitive characters are included
			if(matchFlag > 0){    //more than 0 ，return true
				flag = true;
			}
		}
		return flag;
		 
	}

	 private Map addSensitiveWordToHashMap(Set<String> keyWordSet) {  
			Map sensitiveWordMap = new HashMap(keyWordSet.size());     //Initialize sensitive word containers to reduce expansion 
	        String key = null;    
	        Map nowMap = null;  
	        Map<String, String> newWorMap = null;  
	        //迭代keyWordSet  
	        Iterator<String> iterator = keyWordSet.iterator();  
	        while(iterator.hasNext()){  
	            key = iterator.next();    //sensitive characters  
	            nowMap = sensitiveWordMap;  
	            for(int i = 0 ; i < key.length() ; i++){  
	                char keyChar = key.charAt(i);       //Converted to char type 
	                Object wordMap = nowMap.get(keyChar);
	                  
	                if(wordMap != null){        //If there is the key, direct assignment 
	                    nowMap = (Map) wordMap;  
	                }  
	                else{     //If there is no key ,create a map and make isEnd 0,because it isn't the last one  
	                    newWorMap = new HashMap<String,String>();  
	                    newWorMap.put("isEnd", "0");     
	                    nowMap.put(keyChar, newWorMap);  
	                    nowMap = newWorMap;  
	                }
	                if(i == key.length() - 1){  
	                    nowMap.put("isEnd", "1");    //the last one
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
				nowMap = (Map) nowMap.get(word);     //get the key
				if(nowMap != null){     //if it exists, it is judged whether or not it is the last one
					matchFlag++;     
					if("1".equals(nowMap.get("isEnd"))){       //If it is the last matching rule, the loop is terminated and the matching identifier is returned
						flag = true;       //the end flag is true   
						if(FilterBanedKeywordsServiceImpl.minMatchType == matchType){    //Minimum rules, direct return. The maximum rule needs to be continued
							break;
						}
					}
				}
				else{     //it don't exist,
					break;
				}
			}
			if(matchFlag < 2 || !flag){        //The length must be greater than or equal to 1 
				matchFlag = 0;
			}
			return matchFlag;
		}
}
