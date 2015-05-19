package com.example.ddvoice;

import android.content.Intent;

public class CallView {
	
	MainActivity mActivity;
	
	public CallView(MainActivity activity){
		mActivity=activity;
	}
	
	public void start(){
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_CALL_BUTTON);
		mActivity.startActivity(intent);
	}
}
