package com.example.ddvoice;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;



public class PlayAction {
	  MainActivity mActivity;
	  String mName;
	  String mTitle=null;
	  String mId=null;
	  boolean mIsMuisc;
	  private static Map mVideosMap;
	  private static Map mMusicMap;
	  private ProgressDialog mProgressDialog;
		LooperThread mThread;
	  public PlayAction(String name,MainActivity activity)
	  {
		mName = name;
	    mActivity=activity;
	    mIsMuisc=false;
	  }
	  
	  Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if(mIsMuisc){
					if(PlayMusicById(mName))return;
				}else{
					if(PlayVideoById(mName))return;	
				}					
				mActivity.speak("没有找到你说的东西~_~", true);
				super.handleMessage(msg);
			}
		};
	  
	  public void Play(){
		  if((mName!=null)&&(mName.length()!=0)){
				if(checkSDCardMount()){
					 if(mThread!=null&&mThread.isAlive())return;
					 getMusicByName();
						mProgressDialog =new ProgressDialog(mActivity);
						mProgressDialog.setMessage("正在帮你搜索，请稍候");
						mProgressDialog.show();	
						mThread=new LooperThread();
						mThread.start();
						
				}else{
					mActivity.speak("坑爹呢  你没有SD卡", true);	
				}
			}
	  }
	  
	  private boolean PlayMusicById(String name)
	  {
		 String id=null;
		 id =(String) mMusicMap.get(name);
		 if(id==null) return false;	 
		  if(mId==null) return false;	
		 mActivity.speak("为您播放的是："+mTitle, true);
	     Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mId);
	     Intent intent = new Intent("android.intent.action.VIEW", uri);
	     mActivity.startActivity(intent);
	     mActivity.speak("这歌我也喜欢听", true);
	     return true;
	  }
	 
	  private boolean PlayVideoById(String name)
	  {
		  if(mId==null) return false;	
		 mActivity.speak("为您播放的是："+mTitle, true);
	     Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mId);
	     Intent intent = new Intent("android.intent.action.VIEW", uri);
	     mActivity.startActivity(intent);
	     mActivity.speak("听说这视频不错", true);
	     return true;
	  }
	  
	
	  
	  private void getMusicByName(){
		  Cursor cursor=null;
		  ContentResolver contentResolver = mActivity.getContentResolver();
		    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		    String[] projection = new String[2];
		    projection[0] = "_id";
		    projection[1] = "title";
		    String Where= "title"+" LIKE"+" '%"+ mName +"%'";
		    cursor = contentResolver.query(uri, projection,Where,null,null);
		    if((cursor!=null)&&(cursor.moveToFirst())){
				  int idCoulmn = cursor.getColumnIndex("_id");
				  int id = cursor.getInt(idCoulmn);
				  mId=String.valueOf(id);
				  idCoulmn = cursor.getColumnIndexOrThrow("title");
				  mTitle =cursor.getString(idCoulmn);
			      cursor.close();
			      mIsMuisc=true;
		    }else{
		        if (!cursor.isClosed())
		        	cursor.close();
		    }
	  }
	  
	  private void getVideoByName(){
		  Cursor cursor=null;
		  ContentResolver contentResolver = mActivity.getContentResolver();
		    Uri uri =MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		    String[] projection = new String[2];
		    projection[0] = "_id";
		    projection[1] = "title";
		    String Where= "title"+" LIKE"+" '%"+ mName +"%'";
		    cursor = contentResolver.query(uri, projection,Where,null,null);
		    if((cursor!=null)&&(cursor.moveToFirst())){
				  int idCoulmn = cursor.getColumnIndex("_id");
				  int id = cursor.getInt(idCoulmn);
				  mId=String.valueOf(id);
				  idCoulmn = cursor.getColumnIndexOrThrow("title");
				  mTitle =cursor.getString(idCoulmn);
			      cursor.close();
			      mIsMuisc=false;
		    }else{
		        if (!cursor.isClosed())
		        	cursor.close();
		    }
	  }
	  
	  private Map getAllMusicMap()
	  {
	    HashMap map = new HashMap();
	    ContentResolver contentResolver = mActivity.getContentResolver();
	    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	    String[] projection = new String[2];
	    projection[0] = "_id";
	    projection[1] = "title";
	    Cursor cursor = contentResolver.query(uri, projection, null, null,null);
	    
	    if((cursor!=null)&&(cursor.moveToFirst())){
			  int idCoulmn = cursor.getColumnIndex("_id");
			  int id = cursor.getInt(idCoulmn);
			  idCoulmn = cursor.getColumnIndexOrThrow("title");
			  String title =cursor.getString(idCoulmn);
		      cursor.close();
		      map.put(title,String.valueOf(id));
	    }else{
	        if (!cursor.isClosed())
	        	cursor.close();
	    }
	        return map;     
	  }
	  private Map getAllVideosMap()
	  {
		    HashMap map = new HashMap();
		    ContentResolver contentResolver = mActivity.getContentResolver();
		    Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		    String[] projection = new String[2];
		    projection[0] = "_id";
		    projection[1] = "title";
		    Cursor cursor = contentResolver.query(uri, projection, null, null,null);
		    
		    if((cursor!=null)&&(cursor.moveToFirst())){
				  int idCoulmn = cursor.getColumnIndex("_id");
				  int id = cursor.getInt(idCoulmn);
				  idCoulmn = cursor.getColumnIndexOrThrow("title");
				  String title =cursor.getString(idCoulmn);
			      cursor.close();
			      map.put(title,String.valueOf(id));
		    }else{
		        if (!cursor.isClosed())
		        	cursor.close();
		    }
		        return map;     
	  }

	  private boolean checkSDCardMount() {
	        boolean mExternalStorageAvailable = false;
	        boolean mExternalStorageWriteable = false;
	        
	        String state = Environment.getExternalStorageState();
	        if (Environment.MEDIA_MOUNTED.equals(state)) {    
	            //we can read and write the media    
	            mExternalStorageAvailable = mExternalStorageWriteable = true;
	        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {    
	            //we can only read the media    
	            mExternalStorageAvailable = true;    
	            mExternalStorageWriteable = false;
	        } else {    
	            //something else is wrong.   
	            mExternalStorageAvailable = mExternalStorageWriteable = false;
	        }
	        
	        if (mExternalStorageAvailable && mExternalStorageWriteable) {
	            return true;
	        } else {
	            return false;
	        }
	    }

	  
	  class LooperThread extends Thread {
		  public void run() {
			 if(mMusicMap==null)
			    mMusicMap=getAllMusicMap();
			 if(mVideosMap==null)
			    mVideosMap=getAllVideosMap();
			 if(mIsMuisc)
				 getMusicByName();
			 else
				 getVideoByName();
				handler.sendEmptyMessage(0);
				mProgressDialog.dismiss();
			}
		}
}
