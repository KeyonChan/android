package com.tabhost.other;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	private static final String DBNAME = "jiakao.db";
	private static int db_version = 1;

	//构造方法中新建数据库
	public DBHelper(Context context) {
		super(context, DBNAME, null, db_version);
	}
	//建表，不能关闭
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE shiti (shitiID INT  ,category1 CHAR(1),category2 CHAR(1),shitiType CHAR(1),timu VARCHAR(30),img VARCHAR(10),answer1 VARCHAR(20),answer2 VARCHAR(20),answer3 VARCHAR(20),answer4 VARCHAR(20),rightAnswer CHAR(1),shitiExplain VARCHAR(150))";
		db.execSQL(sql);
	}

	//当数据库的版本号发生增加的时候调用
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("数据库更改！");
		//String sql = "alter table users add account varchar(20)";
		//db.execSQL(sql);
	}

}

