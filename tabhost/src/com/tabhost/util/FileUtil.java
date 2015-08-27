package com.tabhost.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tabhost.ApplicationDemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
/**
 * 
 */
public class FileUtil {
	// 登录后保存用户名到sp,退出后删除，用于判断是否可以直接跳到首页
	public static void setSp(Context context, String fileName, String key,
			String value) {
		SharedPreferences sp = context.getSharedPreferences(fileName,
				context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	//"userInfo"
	public static String getSp(Context context, String fileName, String key) {

		if(!isSpExist(fileName))
			return null;
		SharedPreferences sp = context.getSharedPreferences(fileName,
				context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	//如果用户名不存在则获取手机号
	public static String getUsernameOrPhone(Context context) {
		if(!isSpExist("userInfo"))
			return null;
		SharedPreferences sp = context.getSharedPreferences("userInfo",
				context.MODE_PRIVATE);
		if(sp.getString("username", "")==null || "".equals(sp.getString("username", "")))
			return sp.getString("phone", "");
		else
			return sp.getString("username", "");
	}

	public static Boolean isSpExist(String fileName) {
		File file = new File(ApplicationDemo.SP_PATH + fileName + ".xml");
		if (file.exists())
			return true;
		else
			return false;
	}
	
	public static void deleteSP(String fileName) {
		File file = new File(ApplicationDemo.SP_PATH + fileName + ".xml");
		if (file.exists())
			file.delete();
	}
	
	public static void writeFile(String path, String content){
		FileWriter fw = null;
		try {
			//FileWriter fw = new FileWriter(path,true);   //追加内容，否则重写
			fw = new FileWriter(path);   //生成文件
			fw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			if (fw != null)   //不写的话，如果文件不存在则无指针异常
				try {
					fw.close();
				} catch (IOException e) {
					throw new RuntimeException("关闭失败"); 
				}
		}
	}
	
	public static String readFile(String path){
		FileReader fr = null;
		int len = 0;
		String result = "";
		try {
			File file = new File(path);
			if(!file.exists())
				return null;
			fr = new FileReader(file);
			//fr = new FileReader("test.txt"); //工程根目录
			char[] bufArray = new char[2]; 
			while((len=fr.read(bufArray)) !=-1){    //一次读取一个bufArray的长度
				result += new String(bufArray,0,len);
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally { 
			if (fr != null)   //不写的话，如果文件不存在则无指针异常
				try {
					fr.close();
				} catch (IOException e) {
					throw new RuntimeException("关闭失败"); 
				}
		}
	}
	public static void createFolder(String path){
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}
}
