package cn.chenlin.mobilesafe.ui;

import java.io.File;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.*;
import android.R.string;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AdToolsActivity extends Activity implements OnClickListener {

	protected static final int ERROR = 10;
	protected static final int SUCCESS = 9;
	private TextView tv_adtools_query;
//	private TextView tv_adtools_service_control;
	private ProgressDialog pd;
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case ERROR:
				Toast.makeText(getApplicationContext(), "下载错误", 0).show();
				break;
			case SUCCESS:
				Toast.makeText(getApplicationContext(), "下载完成", 0).show();
				break;

			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.adtools);
		tv_adtools_query=(TextView) this.findViewById(R.id.tv_adtools_query);
//		tv_adtools_service_control=(TextView) this.findViewById(R.id.tv_adtools_service_control);
		
		tv_adtools_query.setOnClickListener(this);
	}

	@Override
	public void onClick(View viewid) {
		switch (viewid.getId()) {
		case R.id.tv_adtools_query:
			if(isDBexist())
			{
				Intent  adtools_query=new Intent(AdToolsActivity.this,QueryNumberActivity.class);
				startActivity(adtools_query);
			}
			else {
				//显示下载对话框
				pd=new ProgressDialog(this);
				pd.setMessage("正在下载数据库文件");
				
				//设置进度条为水平状的
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				
				//开始下载数据库，要开启一个子线程
					new Thread(){
						@Override
						public void run() {
							//getResources()获取config里面的参数
							String  path=getResources().getString(R.string.addressurl);
							String filepath="/sdcard/address.db";
							Message msg=new Message();
							try {
								
								File file = DownLoadFileTask.getFile(path, filepath, pd);
								pd.dismiss();
								
								msg.what=SUCCESS;
								handler.sendMessage(msg);
								
							} catch (Exception e) {
								e.printStackTrace();
								pd.dismiss();
								
								msg.what=ERROR;
								handler.sendMessage(msg);
							}
						}
					}.start();
			}
			break;
		}
	}
	/*
	 * 判断数据库是否存在，如果存在就返回为true
	 */
	public boolean isDBexist()
	{
//		db=SQLiteDatabase.openDatabase("/sdcard/address.db", null, SQLiteDatabase.OPEN_READONLY);
//			if(db.equals(null))
//				return false;
//			else {
//				return true;
//			}
		File file=new File("/sdcard/address.db");
		return file.exists();
	}
}
