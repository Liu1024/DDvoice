package com.example.ddvoice;

public class SearchWeather {

	private String mCity=null,mSourceName=null;
	private String[] mWeatherDate=null,mWeather=null,mTempRange=null,mAirQuality=null,mWind=null,mHumidity=null,mWindLevel=null;
	private MainActivity mActivity=null;
	
	public SearchWeather(String city,String sourceName,String[] weatherDate,String[] weather,String[] tempRange,String[] 
			airQuality,String[] wind,String[] humidity,String[] windLevel,MainActivity activity){
		mCity=city;
		mSourceName=sourceName;
		mWeatherDate=weatherDate;
		mWeather=weather;
		mTempRange=tempRange;
		mAirQuality=airQuality;
		mWind=wind;
		mHumidity=humidity;
		mWindLevel=windLevel;
		mActivity=activity;
	}
	
	public void start(){
		mActivity.speak("以下是"+mCity+"近几天的天气情况,来源："+mSourceName, false);
		for(int i=0;i<6;i++){
			mActivity.speak(mWeatherDate[i]+" "+mWeather[i]+" "+mTempRange[i]+" "+mAirQuality[i]+" "+mWind[i]+" "+mHumidity[i]+" "+mWindLevel[i], false);
		}
	}
	
}
