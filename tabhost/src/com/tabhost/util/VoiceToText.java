package com.tabhost.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.tabhost.R;
import com.tabhost.Tab_IndexActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.Toast;

/**
 * 调用new Daojishi(30000, 1000, hide).start();
 *
 */
public class VoiceToText{
	Context context;
	SpeechRecognizer mIat;
	
	public VoiceToText(Context context){
		this.context = context;
	}
	
	/**
	 * 初始化监听器，离线时使用。
	 */
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Toast.makeText(context, "初始化失败，错误码：", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void voice(RecognizerListener recognizerListener){
		//1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener 
		mIat = SpeechRecognizer.createRecognizer(context, null); 
		//2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类 
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		mIat.setParameter(SpeechConstant.DOMAIN, "iat"); 
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn"); 
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "1");

		// 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		//mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/iflytek/wavaudio.pcm");

		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
		//3.开始听写    
		mIat.startListening(recognizerListener); 
	}
	
	public String printResult(RecognizerResult results) {
		HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
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
		Toast.makeText(context, resultBuffer.toString(), Toast.LENGTH_SHORT).show();
		return resultBuffer.toString();
	}
}
