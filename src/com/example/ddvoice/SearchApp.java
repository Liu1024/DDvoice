package com.example.ddvoice;

import android.content.Intent;
import android.net.Uri;

public class SearchApp {
	private String mPrice,mName;
	MainActivity mActivity;
	
	public SearchApp(String price,String name,MainActivity activity){
		mPrice=price;
		mName=name;
		mActivity=activity;
	}
	
	public void start(){
		mActivity.speak("ÕýÔÚËÑË÷...", false);
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setData(Uri.parse("market://search?q="+mName));
	    mActivity.startActivity(intent);
	}
}
