package com.tabhost.util;

import java.util.Calendar;
import java.util.Locale;

import com.tabhost.R;
import com.tabhost.Tab_IndexActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogUtil {

	public static void showMessageDialog(Context context, String title,String content){
		new AlertDialog.Builder(context). 
		setTitle(title).
		setMessage(content).
		setIcon(R.drawable.ic_launcher).
		setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}

	public static void showEventDialog(Context context, String title,String content, DialogInterface.OnClickListener listener){
		new AlertDialog.Builder(context).
		setTitle(title).
		setMessage(content).
		setIcon(R.drawable.ic_launcher).
		setPositiveButton("确定", listener).
		setNegativeButton("取消", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) { 
			} 
		}).show();
	}

	//DialogUtil.showIntentDialog(this, "提示", "跳转", new Intent(this, MainActivity.class));
	public static void showIntentDialog(final Context context, String title, String content,final Intent intent){
		new AlertDialog.Builder(context).
		setTitle(title).
		setMessage(content).
		setIcon(R.drawable.ic_launcher).
		setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(intent);
			} 
		}).
		setNegativeButton("取消", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) { 
			}
		}).show();
	}

	//只返回Dialog对象，点击空白处自动隐藏
	public static Dialog getProgressDialog(final Context context, String tip) {
		final Dialog dialog = new Dialog(context, R.style.dialog); //样式
		dialog.setContentView(R.layout.progress_dialog_layout); //布局
		dialog.setCancelable(false);
		TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad); //通过dialog获取控件
		titleTxtv.setText(tip);
		//dialog.show();
		return dialog;
	}
	
	//不显示只返回Dialog对象
	public static Dialog showDateDialog(Context context, DatePickerDialog.OnDateSetListener dateSet) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);;
		DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSet, 
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		return datePickerDialog;
	}
}
