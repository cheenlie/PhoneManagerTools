package cn.chenlin.mobilesafe.ui;

import java.io.File;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.*;
import cn.chenlin.mobilesafe.service.AddressService;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ShareCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AdToolsActivity extends Activity implements OnClickListener {

	protected static final int ERROR = 10;
	protected static final int SUCCESS = 9;
	private TextView tv_adtools_query;
	private ProgressDialog pd;
	private TextView tv_adtools_service_status;
	private CheckBox cb_adtools_status_control;
	private Intent serviceIntent;
	private TextView tv_setbg;
	private SharedPreferences sp;
	private TextView tv_change_location;

	private Handler handler = new Handler() {

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
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		
		tv_adtools_service_status = (TextView) this
				.findViewById(R.id.tv_adtools_service_status);
		cb_adtools_status_control = (CheckBox) this
				.findViewById(R.id.cb_adtools_status_control);
		tv_change_location=(TextView) this.findViewById(R.id.tv_adtools_change_location);
		tv_change_location.setOnClickListener(this);
		tv_setbg = (TextView) this.findViewById(R.id.tv_adtools_setbg);

		tv_setbg.setOnClickListener(this); // 和下面的tv_adtools_query的点击事件共用一个onClick（）

		serviceIntent = new Intent(this, AddressService.class);

		cb_adtools_status_control
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton ButtonView,
							boolean isChecked) {
						if (isChecked) {
							startService(serviceIntent);
							tv_adtools_service_status.setTextColor(Color.WHITE);
							tv_adtools_service_status.setText("归属地服务已经开启");
						} else {
							stopService(serviceIntent);
							tv_adtools_service_status.setTextColor(Color.RED);
							tv_adtools_service_status.setText("归属地服务未开启");
						}

					}
				});

		tv_adtools_query = (TextView) this.findViewById(R.id.tv_adtools_query);
		tv_adtools_query.setOnClickListener(this);
	}

	@Override
	public void onClick(View viewid) {
		switch (viewid.getId()) {
		case R.id.tv_adtools_query:
			if (isDBexist()) {
				Intent adtools_query = new Intent(AdToolsActivity.this,
						QueryNumberActivity.class);
				startActivity(adtools_query);
			} else {
				// 显示下载对话框
				pd = new ProgressDialog(this);
				pd.setMessage("正在下载数据库文件");

				// 设置进度条为水平状的
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();

				// 开始下载数据库，要开启一个子线程
				new Thread() {
					@Override
					public void run() {
						// getResources()获取config里面的参数
						String path = getResources().getString(
								R.string.addressurl);
						String filepath = "/sdcard/address.db";
						Message msg = new Message();
						try {

							File file = DownLoadFileTask.getFile(path,
									filepath, pd);
							pd.dismiss();

							msg.what = SUCCESS;
							handler.sendMessage(msg);

						} catch (Exception e) {
							e.printStackTrace();
							pd.dismiss();

							msg.what = ERROR;
							handler.sendMessage(msg);
						}
					}
				}.start();
			}
			break;
		case R.id.tv_adtools_setbg:
			//4.5-2新建一个弹出单选对话框,区别于新建一个xml窗体
			AlertDialog.Builder builder= new Builder(this);
			builder.setTitle("归属地显示风格");
			
			String[] items=new String[]{"半透明","活力橙","苹果绿"};
			//设置单选框提示风格
			builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
				 //显示指定OnClickListener的类型为DialogInterface，要不会报错误
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//获取一个sp的editor
					Editor editor=sp.edit();
					editor.putInt("background", which);
					editor.commit();
				}
			});//默认选择的是第一个，下标为0
			
			//再给窗口设置一个确定按钮
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			//最后把builder展现出来
			builder.show();
			break;
		case R.id.tv_adtools_change_location:
			
			//设置一个移动界面
			
			break;
		}
	}

	/*
	 * 判断数据库是否存在，如果存在就返回为true
	 */
	public boolean isDBexist() {
		// db=SQLiteDatabase.openDatabase("/sdcard/address.db", null,
		// SQLiteDatabase.OPEN_READONLY);
		// if(db.equals(null))
		// return false;
		// else {
		// return true;
		// }
		File file = new File("/sdcard/address.db");
		return file.exists();
	}
}
