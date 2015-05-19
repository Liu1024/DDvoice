package com.example.ddvoice;

public class OpenQA {

	private String mText;
	MainActivity mActivity;
	
	public OpenQA(String text,MainActivity activity){
		mText=text;
		mActivity=activity;
	}
	
	public void start(){
		mActivity.speak(mText, false);
	}
	
}
