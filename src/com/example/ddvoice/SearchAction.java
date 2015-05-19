package com.example.ddvoice;

import android.content.Intent;
import android.net.Uri;



public class SearchAction {
	 MainActivity mActivity;
	 String mKeyword;
	 //String searchEngine;
	
	 public  SearchAction(String name,MainActivity activity)
	  {
		mKeyword = name;
	    mActivity=activity;
	  }
	 
	 public void Search(){		 
		startWebSearch();	
	 }
	
	 private void startWebSearch()
	  {
		 mActivity.speak("正在搜索："+mKeyword+"...", false);
		 Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);	
			intent.setData(Uri.parse("http://m.baidu.com/s?word="+mKeyword)); 
			intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");//设置为系统自带浏览器启动
	    mActivity.startActivity(intent);
	  }
}
