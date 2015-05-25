package com.example.ddvoice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
	private static final String PATH = "http://openapi.baidu.com/public/2.0/bmt/translate";
	private static String API_KEY = "iEUtQwqn0a436q0DDmLYHZeq";//暂时使用别人的注册码
	
    public static String upLoad(String requestText,String from, String to){
    	String result = "";
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", API_KEY));
			params.add(new BasicNameValuePair("q",requestText));
			params.add(new BasicNameValuePair("from", from));
			params.add(new BasicNameValuePair("to", to));
			
			HttpPost request = new HttpPost(PATH); 
			HttpResponse response;
			request.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			response = new DefaultHttpClient().execute(request);
			if(response.getStatusLine().getStatusCode()==200){
				result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
				//解析出翻译的内容
				result = getResult(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
  //解析出翻译的内容
    public static String getResult(String result){
    	try {
			JSONObject object = new JSONObject(result);
			JSONArray arr = object.getJSONArray("trans_result");
			System.out.println("---->"+arr.length());
			JSONObject obj = (JSONObject) arr.get(0);
			result = obj.get("dst").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return result;
    }
}
