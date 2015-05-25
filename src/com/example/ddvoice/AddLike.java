package com.example.ddvoice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class AddLike{//添加点赞至数据库，存入【语句-赞/踩】 的键值对，在显示语句时可以先搜索该数据库，显示出有多少人赞有多少人踩 
	//private EditText et1, et2, et3;
	//private Button b1;
	private String mReaction;
	private MainActivity mActivity;
	public AddLike(String reaction,MainActivity activity){
		mReaction=reaction;
		mActivity=activity;
	}
	//public void onCreate(Bundle savedInstanceState) {
	//	super.onCreate(savedInstanceState);
	//	setContentView(R.layout.add);
	//	this.setTitle("添加收藏信息");
		//et1 = (EditText) findViewById(R.id.EditTextName);
		//et2 = (EditText) findViewById(R.id.EditTextUrl);
		//et3 = (EditText) findViewById(R.id.EditTextDesc);
		//b1 = (Button) findViewById(R.id.ButtonAdd);
		//b1.setOnClickListener(new OnClickListener() {
	public void start(){
		ContentValues values = new ContentValues();
		values.put("reaction", mReaction);
		values.put("like",1);
		DBHelper helper = new DBHelper(mActivity);
		helper.insert(values);
		//mActivity.speak("数据插入成功", false);
		/*Intent intent = new Intent(AddActivity.this,
				QueryActivity.class);
		startActivity(intent);*/
		/*	public void onClick(View v) {
				String name = et1.getText().toString();
				String url = et2.getText().toString();
				String desc = et3.getText().toString();
				
			}*/
		
	}
}