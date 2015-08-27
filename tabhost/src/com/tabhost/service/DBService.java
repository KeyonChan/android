package com.tabhost.service;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.tabhost.other.DBHelper;

public class DBService {
	
	//增删改
	public static void IDU(Context context, String sql) {
		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}
	
	public static ArrayList<HashMap<String, String>> find(Context context, String sql) {
		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i=0;i<cursor.getColumnCount();i++)
				map.put(cursor.getColumnName(i), cursor.getString(i)); //键值对
			list.add(map);
		}
		cursor.close();
		db.close();
		return list;
	}

	public static int findCount(Context context, String tableName){
		SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) as cnt from"+tableName, null);
		int count = 0;
		while (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		//int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

}
