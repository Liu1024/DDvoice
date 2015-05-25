package com.example.ddvoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class GetLocation {//得到地理位置
	
	private LocationManager locationManager;
	private String locationProvider;  
	MainActivity mActivity=null;
	
	public GetLocation(MainActivity activity){
		mActivity=activity;
	}
	
	public void start(){
		//获取地理位置管理器  
        locationManager = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);  
        //获取所有可用的位置提供器  
        List<String> providers = locationManager.getProviders(true);  
        if(providers.contains(LocationManager.GPS_PROVIDER)){  
            //如果是GPS  
            locationProvider = LocationManager.GPS_PROVIDER;  
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){  
            //如果是Network  
            locationProvider = LocationManager.NETWORK_PROVIDER;  
        }else{  
        	mActivity.speak("没有可用的位置提供器", false);
            return ;  
        }  
        //获取Location  
        Location location = locationManager.getLastKnownLocation(locationProvider);  
        if(location!=null){  
            //不为空,显示地理位置经纬度  
            showLocation(location);  
        }  
        //监视地理位置变化  
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);  
          
    }  
	
	
	 /** 
     * 显示地理位置经度和纬度信息 
     * @param location 
     */  
    private void showLocation(Location location){  
        String locationStr = "维度：" + location.getLatitude() +"\n"   
                + "经度：" + location.getLongitude();  
        mActivity.speak(locationStr, false);
        mActivity.speak(LocationToCity(location.getLatitude(),location.getLongitude()), false);
        //postionView.setText(locationStr);  
    }  
    
    
    /** 
     * LocationListern监听器 
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器 
     */  
      
    LocationListener locationListener =  new LocationListener() {  
          
        @Override  
        public void onStatusChanged(String provider, int status, Bundle arg2) {  
              
        }  
          
        @Override  
        public void onProviderEnabled(String provider) {  
              
        }  
          
        @Override  
        public void onProviderDisabled(String provider) {  
              
        }  
          
        @Override  
        public void onLocationChanged(Location location) {  
            //如果位置发生变化,重新显示  
            showLocation(location);  
              
        }

    };  
    
    private String LocationToCity(double latitude,double longitude){//使用百度地图API实现从经纬度转换为城市
    	
    	
    	//listview = (ListView)this.findViewById(R.id.listview);
    	 // String length = mActivity.getResources().getString(R.string.length);
    /*	  
    	  try
    	  {
    	   List<News> newses = NewsService.getLastNews();//得到XML
    	   List<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
    	   for(News news:newses)
    	   {
    	    HashMap<String,Object> item = new HashMap<String,Object>();
    	    item.put("id", news.getId());
    	    item.put("title", news.getTitle());
    	    //item.put("timelength", length+news.getTimelength());
    	    data.add(item);
    	   }
    	   
    	   //SimpleAdapter adapter = new SimpleAdapter(this, data,R.layout.item ,new String[]{"title","timelength"}, new int[]{R.id.title,R.id.timelength});
    	   //listview.setAdapter(adapter);
    	  }catch(Exception e)
    	  {
    	   e.printStackTrace();
    	   
    	  }
    */
    	
    	
    	
    	
    	
		return locationProvider;
    	
    }
    
    /*protected void onDestroy() {  
        super.onDestroy();  
        if(locationManager!=null){  
            //移除监听器  
            locationManager.removeUpdates(locationListener);  
        }  
    }  
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        // Inflate the menu; this adds items to the action bar if it is present.  
        getMenuInflater().inflate(R.menu.main, menu);  
        return true;  
    }  */
	
}