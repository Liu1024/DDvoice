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
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
	
	//from SiriCN
	private ProgressDialog mProgressDialog;//进程提示框
	private MediaPlayer player;//播放音乐
	
	private ListView mListView;
	private ArrayList<SiriListItem> list;
	ChatMsgViewAdapter mAdapter;
	

	//识别结果
	private String recognizerResult;
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
		
		 //听写UI监听器
		private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
			public void onResult(RecognizerResult results, boolean isLast) {
				printResult(results,isLast);
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
		
	//语音识别监听器
		private RecognizerListener recognizerListener = new RecognizerListener() { 
			public void onBeginOfSpeech() {
				//info.makeText(getApplicationContext(), "开始说话", 100).show();
				//showTip("开始说话");
			}	 
			public void onError(SpeechError error) {
				// Tips：
				// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
				// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
				//info.makeText(getApplicationContext(), error.getPlainDescription(true), 1000).show();
				showTip(error.getPlainDescription(true));
			} 
			public void onEndOfSpeech() {
				//info.makeText(getApplicationContext(), "结束说话", 100).show();
				showTip("结束说话");
			} 
			public void onResult(RecognizerResult results, boolean isLast) {
				Log.d(TAG, results.getResultString());
				printResult(results,isLast);

				if (isLast) {
					// TODO 最后的结果
				}
			} 
			public void onVolumeChanged(int volume) {
				showTip("当前正在说话，音量大小：" + volume);
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
			speak("你好，我是小D，您的智能语义助手。", false);
    }

    public void initIflytek(){//初始讯飞设置
    	
    	//找到Siri开关
    	findViewById(R.id.voice_input).setOnClickListener(MainActivity.this);
    	//创建用户语音配置对象后才可以使用语音服务，建议在程序入口处调用。以下appid需要自己去科大讯飞网站申请，请勿使用默认的进行商业用途。
    	SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=553f8bf7");
    }
    
    public void initUI(){//初始化UI和参数
    	recognizerResult="";
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
    
    public void test(){
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
    
    private void printResult(RecognizerResult results,boolean isLast) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		recognizerResult=resultBuffer.toString();
		if(isLast==true)
		speak(recognizerResult, true);
	}
    
    int ret = 0; // 函数调用返回值
    
	@SuppressWarnings("static-access")
	@Override
	public void onClick(View view) {//语音识别过程
		player = MediaPlayer.create(MainActivity.this, R.raw.begin);
		player.start();
		test();
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
