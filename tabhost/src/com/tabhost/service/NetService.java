package com.tabhost.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.tabhost.R;
import com.tabhost.Tab_IndexActivity;
import com.tabhost.TestComment;
import com.tabhost.other.LruImageCache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

public class NetService {
	
	/*public static void showVolleyImg(RequestQueue requestQueue, NetworkImageView img, String url){
		LruImageCache lruImageCache = LruImageCache.getInstance();
		ImageLoader imageLoader = new ImageLoader(requestQueue,lruImageCache);
		img.setDefaultImageResId(R.drawable.ic_launcher);
		img.setErrorImageResId(R.drawable.ic_launcher);		
		img.setImageUrl(url, imageLoader);
	}

	public static StringRequest getVolleyReq(final Context context,String url, final Map<String, String> param, int timeout, Response.Listener<String> sucListener,final String errorTip) {
		StringRequest request = new StringRequest(Method.POST, "http://192.168.191.1/cjytest2/1/common/test",
				sucListener, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if(errorTip!=null)
					Toast.makeText(context, errorTip, 0).show();
			}
		}) {
			@Override
			protected Map<String, String> getParams()
					throws AuthFailureError {
				return param;
			}
		};
		request.setRetryPolicy(new DefaultRetryPolicy(1000*timeout, 1, 1.0f));
		return request;
	}*/

	public static String clientGet(String path) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(path);
			HttpResponse response = client.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			System.out.println(code);
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String result = readInputStream(is);
				is.close();
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String clientPost(String path, ArrayList<NameValuePair> parameters) {
		try {
			// 1.打开浏览器
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(path);
			/*// 3.设置参数
			ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("username", username));*/
			httpPost.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));
			// 4.敲回车
			HttpResponse response = client.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			System.out.println(code);
			if (code == 200) {
				InputStream is = response.getEntity().getContent();
				String result = readInputStream(is); //调用
				is.close();
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/*//可以验证Jsonarray是否为空，不为空返回Jsonarray
	public static String getJsonarrayFromJsonobject(String jo, String key){
		try {
			JSONObject jsonObject = new JSONObject(jo); //地图回传的是JSONObject格式，转成JSONArray
			String jsonArrayStr = jsonObject.getJSONArray(key).toString();
			if(jsonArrayStr==null)
				return null;
			else
				return jsonArrayStr;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}*/

	@SuppressLint("NewApi")
	public static String uploadToPhp(String postUrl, String uploadFile){
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try{
			URL url = new URL(postUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* Output to the connection. Default is false,
             set to true because post method must write something to the connection */
			con.setDoOutput(true);
			/* Read from the connection. Default is true.*/
			con.setDoInput(true);
			/* Post cannot use caches */
			con.setUseCaches(false);
			/* Set the post method. Default is GET*/
			con.setRequestMethod("POST");
			/* 设置请求属性 */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			/*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
			/* 设置DataOutputStream，getOutputStream中默认调用connect()*/
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " +
					"name=\"file\";filename=\"headphoto.jpg"
					+ "\"" + end);
			ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入8192bytes */
			int bufferSize = 8192;
			byte[] buffer = new byte[bufferSize];   //8k
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1){
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* 关闭流，写入的东西自动生成Http正文*/
			fStream.close();
			/* 关闭DataOutputStream */
			ds.close();
			/* 从返回的输入流读取响应信息 */
			InputStream is = con.getInputStream();  //input from the connection 正式建立HTTP连接
			return readInputStream(is);
		} catch (Exception e){
			System.out.println(e.toString());
			return null;
		}
	}

	public static String getValueFromJsonobject(String jo, String key){
		try {
			JSONObject jsonObject = new JSONObject(jo); //地图回传的是JSONObject格式，转成JSONArray
			String jsonArrayStr = jsonObject.getString(key);
			if(jsonArrayStr==null)
				return null;
			else
				return jsonArrayStr;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<Map<String,String>> jsonObjectToList(String jsonStr, String key) {
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			JSONObject jo = new JSONObject(jsonStr); //地图回传的是JSONObject格式，转成JSONArray
			JSONArray jsonArray = jo.getJSONArray(key);
			JSONArray keys = jsonArray.getJSONObject(0).names(); //key集合，固定
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();//放里面,每次重新定义,否则list会重复
				for (int j = 0; j < keys.length(); j++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i); //要先转换成JSONObject
					map.put(keys.getString(j), jsonObject.getString(keys.getString(j)));
				}
				list.add(map);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("list is null------------------");
			return null;
		}
	}

	public static ArrayList<Map<String,String>> jsonArrayToList(String jsonStr) {
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			JSONArray keys = jsonArray.getJSONObject(0).names(); //key集合，JSONArray类型
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();//放里面,每次重新定义,否则list会重复
				for (int j = 0; j < keys.length(); j++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i); //要先转换成JSONObject
					map.put(keys.getString(j), jsonObject.getString(keys.getString(j)));
				}
				list.add(map);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("list is null------------------");
			return null;
		}
	}

	public static void addToList(List<Map<String, String>> list, String jsonStr){
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();//放里面,每次重新定义,否则list会重复
				JSONArray keys = jsonArray.getJSONObject(i).names(); 
				for (int j = 0; j < keys.length(); j++) {//key数量
					JSONObject jsonObject = jsonArray.getJSONObject(i); //要先转换成JSONObject
					map.put(keys.getString(j), jsonObject.getString(keys.getString(j)));
				}
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("list is null------------------");
		}
	}

	public static LinkedList<Map<String,String>> jsonArrayToLinkedList(String jsonStr) {
		LinkedList<Map<String,String>> list = new LinkedList<Map<String,String>>();
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			JSONArray keys = jsonArray.getJSONObject(0).names(); //key集合，JSONArray类型
			for (int i = 0; i < jsonArray.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();//放里面,每次重新定义,否则list会重复
				for (int j = 0; j < keys.length(); j++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i); //要先转换成JSONObject
					map.put(keys.getString(j), jsonObject.getString(keys.getString(j)));
				}
				list.add(map);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("list is null------------------");
			return null;
		}
	}

	public static String listmapToJson(List<Map<String, String>> list){
		try {
			String result="";
			JSONArray jsonArray = new JSONArray();
			Set<String> set = list.get(0).keySet();
			for(Map map : list){
				JSONObject jsonObject = new JSONObject();
				for(String key : set)
					jsonObject.put(key, map.get(key));
				jsonArray.put(jsonObject);
			}
			return jsonArray.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	//传入InputStream，ByteArrayOutputStream输出.
	public static String readInputStream(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			//read()输入流中每次读取最多1024字节，并将其存储在数组buffer 中,返回int
			while ((len = is.read(buffer)) != -1) {
				System.out.println("buffer.length==="+buffer.length+" len="+len);
				baos.write(buffer, 0, len); //将每次新生成的buffer拼接到输出流
			}
			is.close();
			byte[] result = baos.toByteArray();
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "转换失败";
		}
	}

	//获取服务器端返回码
	public static String getReturnStatus(String content) {
		return getValueFromJsonobject(content, "status");
	}

	public static String getReturnMsg(String content) {
		return getValueFromJsonobject(content, "msg");
	}

	public static String getReturnTotal(String content) {
		return getValueFromJsonobject(content, "total");
	}

	//获取服务器端返数据
	public static String getReturnRows(String content) {
		return getValueFromJsonobject(content, "rows");
	}

}
