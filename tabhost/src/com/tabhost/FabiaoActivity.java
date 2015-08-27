package com.tabhost;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.util.PreferencesCookieStore;
import com.tabhost.download.DownloadManager;
import com.tabhost.download.DownloadService;
import com.tabhost.util.CommonUtil;
import com.tabhost.util.ImgUtil;
import com.tabhost.view.MyGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class FabiaoActivity extends Activity {
	MyGridView gridview;
	PopupWindow popupWindow;
	LinkedList<Bitmap> bmplist;
	MyAdapter adapter;
	Bitmap add;
	String albumPath, cameraPath = Environment.getExternalStorageDirectory()
			+ "/myPicture.jpg";
	private final int CAMERA_REQUEST = 0;
	private final int ALBUM_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fabiao);
		gridview = (MyGridView) findViewById(R.id.gridview);
		bmplist = new LinkedList<Bitmap>();
		Bitmap add = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.add);
		bmplist.add(add);
		adapter = new MyAdapter();
		gridview.setAdapter(adapter);
	}

	private class MyAdapter extends BaseAdapter {
		public int getCount() {
			return bmplist.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// 返回view
		public View getView(final int position, final View convertView,
				ViewGroup parent) {
			View view = View.inflate(getApplicationContext(),
					R.layout.item_photo_grid, null);
			ImageView img = (ImageView) view.findViewById(R.id.img);
			ImageView delete = (ImageView) view.findViewById(R.id.delete);
			img.setImageBitmap(bmplist.get(position));
			if (position == bmplist.size() - 1) {
				delete.setVisibility(View.GONE);
				img.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						showPicPopwindow();
					}
				});

			}
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					/*
					 * if(position==bmplist.size()-1){ showPicPopwindow(); }
					 */
					bmplist.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			return view;
		}

	}

	private void showPicPopwindow() {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.pop_pic_way, null);
		final LinearLayout black_ll = (LinearLayout) view
				.findViewById(R.id.black_ll);
		Button camera = (Button) view.findViewById(R.id.camera);
		Button album = (Button) view.findViewById(R.id.album);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		// view_layout是显示的布局并设置根布局背景为透明，android:background="#b0000000"
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// view是点击的控件，showAtLocation(view, Gravity.BOTTOM |
		// Gravity.CENTER_HORIZONTAL, 0, 0);
		popupWindow.showAtLocation(gridview, Gravity.FILL, 0, 0);
		black_ll.setOnClickListener(new OnClickListener() { // 空白处取消
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
		camera.setOnClickListener(new OnClickListener() { // 空白处取消
			@Override
			public void onClick(View arg0) {
				// 检测sd是否可用
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(FabiaoActivity.this, "SD卡不可用！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				// 设置输出路径后可以得到原图；无法存入项目而要sd
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(cameraPath)));
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
				startActivityForResult(intent, CAMERA_REQUEST);
			}
		});
		album.setOnClickListener(new OnClickListener() { // 空白处取消
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, ALBUM_REQUEST);
			}
		});
		cancel.setOnClickListener(new OnClickListener() { // 空白处取消
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		popupWindow.dismiss();
		if (resultCode != Activity.RESULT_OK)
			return;
		// intent设置输出路径后，data会等于null，这里不能加data != null
		if (requestCode == CAMERA_REQUEST) {
			Bitmap newBmp = ImgUtil.getFangBitmap(BitmapFactory
					.decodeFile(cameraPath));
			bmplist.add(bmplist.size() - 1, newBmp);
			adapter.notifyDataSetChanged();
		}
		if (requestCode == ALBUM_REQUEST) {
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri, null, null, null,
					null);
			cursor.moveToFirst();
			albumPath = cursor.getString(1);
			System.out.println("File path is----->" + albumPath);
			Bitmap newBmp = ImgUtil.getFangBitmap(BitmapFactory
					.decodeFile(albumPath));
			bmplist.add(bmplist.size() - 1, newBmp);
			adapter.notifyDataSetChanged();
			cursor.close();
		}
	}
}
