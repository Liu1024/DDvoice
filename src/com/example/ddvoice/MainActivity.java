package com.example.ddvoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.ddvoice.util.SystemUiHider;




import com.iflytek.cloud.*;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.example.ddvoice.JsonParser;









import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
//import android.speech.SpeechRecognizer;//不用这个
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements OnItemClickListener ,OnClickListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
	
	//语义
	
	
	
	public static boolean serviceFlag=false;//表示是否在一项服务中 
	private int mainServiceID=0;//表示目前对话的上下文是在哪一项主服务中
	private int branchServiceID=0;//表示目前对话的上下文是在主服务中的哪一项分支服务中
	
	public static JSONObject semantic = null,slots =null,answer=null;public static String operation = null,service=null;
	public static String receiver=null, name = null,price=null,code=null,song = null,keywords=null,content=null,
			url=null,text=null;
	
	
	
	private TextUnderstander mTextUnderstander;// 语义理解对象（文本到语义）。
	
	
	//from SiriCN
	private ProgressDialog mProgressDialog;//进程提示框
	private MediaPlayer player;//播放音乐
	
	private ListView mListView;
	private ArrayList<SiriListItem> list;
	ChatMsgViewAdapter mAdapter;



	public static  String SRResult="";	//识别结果
	private static String SAResult="";//语义识别结果
	private static String TAG = MainActivity.class.getSimpleName();
	//Toast提示消息
	private Toast info;
	//文本区域
	private TextView textView;
	//语音识别
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
		private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// 引擎类型
		private String mEngineType = SpeechConstant.TYPE_CLOUD;
		private SharedPreferences mSharedPreferences;
		
		
	//语音识别监听器
		private RecognizerListener recognizerListener = new RecognizerListener() { 
			public void onBeginOfSpeech() {
				//info.makeText(getApplicationContext(), "开始说话", 100).show();
			}	 
			public void onError(SpeechError error) {
				// Tips：
				// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
				// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
				//info.makeText(getApplicationContext(), error.getPlainDescription(true), 1000).show();
				showTip(error.getPlainDescription(true));
				speak("没有听到您说话。",false);
				//startSpeenchRecognition();
			} 
			public void onEndOfSpeech() {
				//info.makeText(getApplicationContext(), "结束说话", 100).show();
				showTip("结束说话");
			} 
			public void onResult(RecognizerResult results, boolean isLast) {
				//Log.d("dd", results.getResultString());
				printResult(results,isLast);

				if (isLast) {
					// TODO 最后的结果
				}
			} 
			public void onVolumeChanged(int volume) {
				showTip("请对着mic说话，当前音量大小：" + volume);
				//info.makeText(getApplicationContext(), "当前正在说话，音量大小：" + volume, 100).show();
			} 
			public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			}
		};



	
	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {
	
		 
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};

	//初始化监听器（文本到语义）。
    private InitListener textUnderstanderListener = new InitListener() {
		public void onInit(int code) {
			Log.d(TAG, "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		//showTip("初始化失败,错误码："+code);
				Log.d("dd","初始化失败,错误码："+code);
        	}
		}
    };
	
	
	//private SemanticAnalysis semanticAnalysis;//语义分析实例
	
	
  
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		info=Toast.makeText(this, "", Toast.LENGTH_SHORT);
			/*mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("正在初始化，请稍候…… ^_^");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();*/
			showTip("初始化中...");
			//info.makeText(getApplicationContext(), "初始化中...", 5).show();
	       // showTip("hai");
			initIflytek();
			initUI();
			speechRecognition();
			//mProgressDialog.dismiss();
			showTip("初始化完毕");
			//info.makeText(getApplicationContext(), "初始化完毕", 5).show();
			player = MediaPlayer.create(MainActivity.this, R.raw.lock);
			player.start();
			speak("你好，我是小D，您的智能语音助手。", false);
			//runOnUiThread(new Runnable() {
				//@Override
				//public void run() {
					// TODO Auto-generated method stub
					//xiaoDReaction();//启动对话管理系统
				//}
			//});
			
		/*	new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true)
					xiaoDReaction();
				}});*/
    }
  

    public void xiaoDReaction() {
    
			/*	JSONObject semantic = null,slots =null;String operation = null,service=null;
				String name = null,code=null,song = null,keywords=null,content=null;*/
			
				try {
					JSONObject SAResultJson = new JSONObject(SAResult);
					operation=SAResultJson.optString("operation");
					service=SAResultJson.optString("service");
					semantic=SAResultJson.optJSONObject("semantic");
					answer=SAResultJson.optJSONObject("answer");
					text=answer.optString("text");
					slots=semantic.optJSONObject("slots");
					receiver=slots.optString("receiver");
					name = slots.optString("name");
					price= slots.optString("price");
					code = slots.optString("code");
					song = slots.optString("song");
					keywords=slots.optString("keywords");
					content=slots.optString("content");
					url=slots.optString("url");
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//speak("获取数据有问题",false);
					e.printStackTrace();
				}
				SRResult=null;//置空
				SAResult=null;
				speak("service:"+service+" operation:"+operation,false);
			if(serviceFlag==false){//如果不在一项服务中才进行服务的判断
				//speak("判断服务类型",false);
				switch(service){
				
				
				case "telephone":{//电话相关服务
					
					switch(operation){
					
						case "CALL":{	//打电话
										//必要条件【电话号码code】
										//可选条件【人名name】【类型category】【号码归属地location】【运营商operator】【号段head_num】【尾号tail_num】
										//可由多个可选条件确定必要条件
							//speak("name:"+name+"code:"+code,false);
							CallAction callAction=new CallAction(name,code,this);//目前可根据名字或电话号码拨打电话
							callAction.start();
							break;
						}
						
						case "VIEW":{	//查看电话拨打记录
										//必要条件无
										//可选条件【未接电话】【已拨电话】【已接电话】
							CallView callview =new CallView(this);
							callview.start();
							break;
						}
						
						default :break;
					
					}
					
					break;
				}
				
				case "message":{
					
					switch(operation){
					
						case "SEND":{//发送短信
						
							SendMessage sendMessage = new SendMessage(name,code,content,MainActivity.this);
							sendMessage.start();
							break;
						}
						
						case "VIEW":{//查看发送短信页面
						
							MessageView messageView=new MessageView(this);
							messageView.start();
							break;
						}
						
						
						
						case "SENDCONTACTS":{//发送名片,目前只能识别：名字发给名字
							SendContacts sendContacts = new SendContacts(name,receiver,this);
							sendContacts.start();
							break;
						}
						default :break;
					}
			
					break;
				}
				
				case "app":{
					
					switch(operation){
						
						case "LAUNCH":{//打开应用
							OpenAppAction openApp = new OpenAppAction(name,MainActivity.this);
							openApp.start();
							break;
						}
						
						case "QUERY":{//应用中心搜索应用
							SearchApp searchApp = new SearchApp(price,name,this);
							searchApp.start();
							break;
						}
						
						default:break;
					
					}
					break;
				}
				
				case "website":{
					
					switch(operation){
					
						case "OPEN":{//打开指定网址
						
							OpenWebsite openWebsite=new OpenWebsite(url,name,this);
							openWebsite.start();
							break;
						}
					
						default:break;
					}
					
					break;
				}
				
				case "websearch":{
					
					switch(operation){
					
						case "QUERY":{//搜索
							
							SearchAction searchAction =new SearchAction(keywords,MainActivity.this);
							searchAction.Search();
							break;
						}
						
						default:break;
					
					}
							
					
					break;
				}
				
				case "openQA":{
					
					switch(operation){
					
					case "ANSWER":{//智能问答
						
						OpenQA openQA = new OpenQA(text,this);
						openQA.start();
						
						break;
					}
					
					default:break;
					}
					
					break;
				}
				
				default:{
					speak("不知道您要干嘛，不过我想过一段时间我就会懂了。",false);
					break;
				}
			}
	}//结束某项服务才跳出
}
				
    	//});
			
			
			
	
		/*	if(operation.equals("LAUNCH")){//打开应用
				speak("好的，为您启动"+name+"...",false);
				OpenAppAction openApp = new OpenAppAction(name,MainActivity.this);
				openApp.runApp();
			}
			if(operation.equals("PLAY")){//播放音乐或视频
				speak("并不知道怎么做...",false);
				if(service.equals("music")){
					PlayAction playAction= new PlayAction(song,MainActivity.this);
					playAction.Play();
				}
				if(service.equals("video")){
					PlayAction playAction= new PlayAction(keywords,MainActivity.this);
					playAction.Play();
				}
			}
			if(operation.equals("QUERY")){//搜索
				speak("好的，正在搜索"+keywords+"...",false);
				SearchAction searchAction =new SearchAction(keywords,MainActivity.this);
				searchAction.Search();
			}*/
			
		
	//}
    
    public void initIflytek(){//初始讯飞设置
    	
    	//找到Siri开关
    	findViewById(R.id.voice_input).setOnClickListener(MainActivity.this);
    	//创建用户语音配置对象后才可以使用语音服务，建议在程序入口处调用。以下appid需要自己去科大讯飞网站申请，请勿使用默认的进行商业用途。
    	SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=553f8bf7");
    
    }
    
    public void initUI(){//初始化UI和参数
    	SRResult="";
    	list = new ArrayList<SiriListItem>();
		mAdapter = new ChatMsgViewAdapter(this, list);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setFastScrollEnabled(true);
		registerForContextMenu(mListView);
    }
    
    public void speechRecognition(){//语音识别初始化
    
    	//1.创建SpeechRecognizer对象，第二个参数： 本地听写时传InitListener
    	mIat= SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);
    	// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
    	mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
    	
    }
    
    public void startSpeenchRecognition(){//语音识别
    	player = MediaPlayer.create(MainActivity.this, R.raw.begin);
		player.start();
    	// 显示听写对话框
    	mIatDialog.setListener(recognizerDialogListener);
		//mIatDialog.show();
		ret = mIat.startListening(recognizerListener);
		if (ret != ErrorCode.SUCCESS) {
			Log.d(TAG, ""+ret);
			showTip("听写失败,错误码：" + ret);
			//info.makeText(getApplicationContext(), "听写失败,错误码：" + ret, 100).show();
		}
		
    }
    
    //语音识别结果监听器
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results,isLast);//得到识别结果 
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			speak(error.getPlainDescription(true),true);
			info.makeText(getApplicationContext(), "error.getPlainDescription(true)", 1000).show();
			//showTip(error.getPlainDescription(true));
		}

	};
	
    
    
    //开始语义识别
    private void startSemanticAnalysis(){
    	//semanticAnalysis=new SemanticAnalysis();
    	//SAResult=semanticAnalysis.getSAResult("我是刘冬冬");//开始语义分析
    	//UnderstanderDemo testSA=new UnderstanderDemo();
    	//SAResult=testSA.startSA("我是刘冬冬");
    	
    	
    	
    	/*Intent SAActivity = new Intent(MainActivity.this,SemanticAnalysis.class);
    	SAActivity.putExtra("SRResult", SRResult);
    	Log.d("dd","识别结果："+SRResult);
    	startActivityForResult(SAActivity,0 );*/
    
    	//onActivityResult(0, 0, SAActivity);
    	
    /*	Intent SAActivity = new Intent(MainActivity.this,SemanticAnalysis.class);
    	startActivity(SAActivity);
    	
    	SemanticAnalysis semanticAnalysis = new SemanticAnalysis();
    	SAResult=semanticAnalysis.SAResult;
    	speak(SAResult, false);*/
    	
    	
    	// SRResult=MainActivity.SRResult;
 	
 		//Log.d("dd","SRResult:"+SRResult);
 		ret=0 ;
 		
 		mTextUnderstander = TextUnderstander.createTextUnderstander(MainActivity.this, textUnderstanderListener);
 		
 		startAnalysis();
    }
    
  //开始语义分析
  	private void startAnalysis(){
  		
  		mTextUnderstander.setParameter(SpeechConstant.DOMAIN,  "iat");
  		if(mTextUnderstander.isUnderstanding()){
  			mTextUnderstander.cancel();
  			//showTip("取消");
  			Log.d("dd","取消");
  		}else {
  			ret = mTextUnderstander.understandText(SRResult, textListener);
  			if(ret != 0)
  			{
  				//showTip("语义理解失败,错误码:"+ ret);
  				Log.d("dd","语义理解失败,错误码:"+ ret);
  			}
  		}
  		/*ret = mTextUnderstander.understandText(SRResult, textListener);
  		if(ret != 0)
  		{
  			showTip("语义理解失败,错误码:"+ ret);
  			
  		}*/
  	}
  	 //识别回调
      private TextUnderstanderListener textListener = new TextUnderstanderListener() {
  		
  		public void onResult(final UnderstanderResult result) {
  	       	runOnUiThread(new Runnable() {
  					
  					public void run() {
  						if (null != result) {
  			            	// 显示
  							//Log.d(TAG, "understander result：" + result.getResultString());
  							String text = result.getResultString();
  							SAResult=text;
  							//Log.d("dd","SAResult:"+SAResult);
  							if (TextUtils.isEmpty(text)) {
  								//Log.d("dd", "understander result:null");
  								//showTip("识别结果不正确。");
  							}
  							//mainActivity.speak();
  							speak(SAResult,false);
  							xiaoDReaction();
  							//finish();
  			            } 
  					}

					

					/*private void dialogueManagement(int mainServiceID,int branchServiceID) {//对话管理器
						// TODO Auto-generated method stub
						if(mainServiceID==1){
							if(branchServiceID==1){//进入了打电话服务，必要条件是【电话号码】,可选条件有【号码归属地】【运营商】【号段】【尾号】，
								//可由多个可选条件确定必要条件
								
							}
							if(branchServiceID==2){//进入了查看电话播放记录
								
							}
							
						}
						if(mainServiceID==2){//进入了发短信服务，必要条件是电话号码和短信内容
							
						}
					}*/
  				});
  		}
  		
  		public void onError(SpeechError error) {
  			//showTip("onError Code："	+ error.getErrorCode());
  			Log.d("dd","onError Code："	+ error.getErrorCode());
  		}
  	};
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){//重写onActivityResult
        if(requestCode == 0){
            //System.out.println("REQUESTCODE equal");
            if(resultCode == 0){
                 //    System.out.println("RESULTCODE equal");
            	SAResult = data.getStringExtra("SRResult");
            }
        }
    }
    
   
    
    private void printResult(RecognizerResult results,boolean isLast) {
		String text = JsonParser.parseIatResult(results.getResultString());

		//Log.d("dd","text:"+text);
		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			Log.d("dd","json:"+results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		SRResult=resultBuffer.toString();
		if(isLast==true){
		speak(SRResult, true);
		startSemanticAnalysis();
		}
	}
    
    int ret = 0; // 函数调用返回值
    
	@SuppressWarnings("static-access")
	@Override
	public void onClick(View view) {//语音识别过程
		
		startSpeenchRecognition();
		
		
		
		
		
		//以下代码不能直接放在这里，不然出错，？？？
		/*info.makeText(getApplicationContext(), "5", 1000).show();
		// TODO Auto-generated method stub
		if(view.getId()==R.id.voice_input){
			//3.开始听写
			
			setParam();
			info.makeText(getApplicationContext(), "开始听写", 1000).show();
				// 不显示听写对话框
				ret = mIat.startListening(recognizerListener);
				if (ret != ErrorCode.SUCCESS) {
					Log.d(TAG, ""+ret);
					//showTip("听写失败,错误码：" + ret);
				} else {
					//showTip("成功");
				}
			}*/

	}

	public void setParam(){
		// 清空参数
				mIat.setParameter(SpeechConstant.PARAMS, null);

				// 设置听写引擎
				mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
				// 设置返回结果格式
				mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

				String lag = mSharedPreferences.getString("iat_language_preference",
						"mandarin");
				if (lag.equals("en_us")) {
					// 设置语言
					mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
				} else {
					// 设置语言
					mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
					// 设置语言区域
					mIat.setParameter(SpeechConstant.ACCENT, lag);
				}
				// 设置语音前端点
				mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
				// 设置语音后端点
				mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
				// 设置标点符号
				mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
				// 设置音频保存路径
				mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
				// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
				// 注：该参数暂时只对在线听写有效
				mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	
	
    //from SiriCN
	public void speak(String msg, boolean isSiri) {//
		//info.makeText(getApplicationContext(), "here", 1000).show();
		addToList(msg, isSiri);//添加到对话列表
		//if(isHasTTS)
		//mSiriEngine.SiriSpeak(msg);
	}
	
	private void addToList(String msg, boolean isSiri) {
		//
		list.add(new SiriListItem(msg, isSiri));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(list.size() - 1);
	}
    
	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
	
	private void showTip(final String str) {
		info.setText(str);
		info.show();
	}
	
	
}
