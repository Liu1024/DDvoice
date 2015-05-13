package com.example.ddvoice;

import java.util.List;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

public class SendMessage {
	private String mPerson;
    private String number;
    private String mcontent;
    MainActivity mActivity;
    
    
    public SendMessage(String person,String content,MainActivity activity){
    	 mPerson = person;
    	 mcontent=content;
 	    mActivity=activity;
    }
    
    public void send(){
    	if ((mPerson == null) || (mPerson.equals("")))
	    {
	      
	    }else{
	    	 mPerson=mPerson.trim();
	    	 number=null; 
	    	 if(isPhoneNumber(mPerson)){
	    		 number=mPerson;
	    	 }
	    	 else
	    		 number=getNumberByName(mPerson,mActivity);
	    	 if(number == null)
	         {
	          // mPerson = null;
	           //VoiceFunction fun=new VoiceFunction(mActivity,null);
	          // fun.getVoiceResponse("查询不到  你要打给谁呢","contact", handler);
	           mActivity.speak("找不到"+mPerson, false);
	         }else{	    
	        	 //发短信
	        	// mActivity.speak("即将拨给"+mPerson+"...", false);
	        	 //Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("tel:" + number,"content:"+mcontent));        	         
	 	        //mActivity.startActivity(intent);
	    		 //new VoiceDialog(mActivity,"您是要呼叫"+mPerson+"\n号码为"+number,rightFn,wrongFn,null);	   
	        	 Log.d("dd","达琼电话："+number);
	        	 Log.d("dd","短信内容："+mcontent);
	        	 SmsManager smsManager = SmsManager.getDefault();
	        	 if(mcontent.length() > 70) {
                     List<String> contents = smsManager.divideMessage(mcontent);
                     for(String sms : contents) {
                         smsManager.sendTextMessage(number, null, sms, null, null);
                         insertDB(number,sms);
                     }
                 } else {
                  smsManager.sendTextMessage(number, null, mcontent, null, null);
                  insertDB(number,mcontent);
                 }
	        	 
	        }
	    }
    }
    
    private void insertDB(String number,String content){//将发送的短信插入系统数据库中，使其在短信界面显示 
    	//////////////////////会抛出null的异常---已解决--- mActivity.getContentResolver()才可以
    	try{
	    	ContentValues values = new ContentValues();
	    	values.put("date", System.currentTimeMillis());
	    	 //阅读状态              
	        values.put("read", 0);             
	         //1为收 2为发             
	       values.put("type", 2);           
	         //送达号码              
	      // values.put("status", -1);
	       values.put("address",number);             
	         //送达内容            
	       values.put("body", content);             
	         //插入短信库    
	      // getContentResolver
	       mActivity.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	       mActivity.speak("短信发送成功",false);
    	}catch (Exception e) { 
            Log.d("dd", "插入数据库问题："+e.getMessage()); 
    	  }
    }
    
    /*private ContentResolver getContentResolver() {
		// TODO Auto-generated method stub
		return null;
	}*/

	private boolean isPhoneNumber(String num)
	{
		//return num.matches("\\d+$");
		return num.matches("^\\d+\\D?$");
	}
    
    private  String getNumberByName(String name, Context context)
	  {
		 Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
		  ContentResolver  resolver  = context.getContentResolver();
		  Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts._ID}, null, null, null);  
		  if((cursor!=null)&&(cursor.moveToFirst())){
			  int idCoulmn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			  long id = cursor.getLong(idCoulmn);
		      cursor.close();
		      cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  new String[]{"data1"}, "contact_id = ?",  new String[]{Long.toString(id)}, null);
		      if ((cursor != null) && (cursor.moveToFirst()))
		      {
		        int m = cursor.getColumnIndex("data1");
		        String num = cursor.getString(m);
		        cursor.close();
		       return num;
		      }	      
		  }
		  return null;
	  }
}
