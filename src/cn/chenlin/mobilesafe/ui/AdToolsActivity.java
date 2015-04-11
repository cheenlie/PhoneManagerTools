package cn.chenlin.mobilesafe.ui;

import java.io.File;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.*;
import cn.chenlin.mobilesafe.service.AddressService;
import cn.chenlin.mobilesafe.service.BackupSmsService;
import cn.chenlin.mobilesafe.service.SmsInfoService;
import android.R.string;
import android.R.style;
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
import android.os.Looper;
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
	private TextView tv_sms_backup;
	private TextView tv_sms_restore;
	private  SmsInfoService smsInfoService;

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
		tv_sms_backup=(TextView) findViewById(R.id.tv_adtools_sms_backup);
		tv_sms_restore=(TextView) findViewById(R.id.tv_adtools_sms_restore);
		tv_sms_backup.setOnClickListener(this);
		tv_sms_restore.setOnClickListener(this);
		
		tv_adtools_service_status = (TextView) this
				.findViewById(R.id.tv_adtools_service_status);
		cb_adtools_status_control = (CheckBox) this
				.findViewById(R.id.cb_adtools_status_control);
		tv_change_location=(TextView) this.findViewById(R.id.tv_adtools_change_location);
		tv_change_location.setOnClickListener(this);
		tv_setbg = (TextView) this.findViewById(R.id.tv_adtools_setbg);

		tv_setbg.setOnClickListener(this); // 和下面的tv_adtools_query的点击事件共用一个onClick（）


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
			Intent intent=new Intent(this,DragViewActivity.class);
			startActivity(intent);
			
			break;
		case R.id.tv_adtools_sms_backup:
			//备份需要把短信内容转换成xml文件
			
			Intent intent1 = new Intent(this, BackupSmsService.class);
			startService(intent1);
			
			break;
		case R.id.tv_adtools_sms_restore:
			//读取xml文件
			//解析xml文件后插入到短信应用程序中	
			//防止还原途中被用户中断，因此采用弹出对话框形式，让程序自己关闭，用户不能控制
			final ProgressDialog pd=new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setCancelable(false);//不能被取消
			pd.setMessage("正在还原短信");
			pd.show();//把对话框显示出来
			smsInfoService=new SmsInfoService(this);
			//是个耗时操作，放在子线程中运行
			new Thread(){
				//在Thread中必须重写run方法，要不没法识Threa外的函数变量
				@Override
				public void run() {
					super.run();
			System.out.println("huifuzhong");		
					try {
						smsInfoService.restoreSms("/sdcard/smsbackup.xml",pd);
						pd.dismiss();
						//如果成功了就弹出恢复成功对话框
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "短信恢复完成", 1).show();
						Looper.loop();//手动的轮循一下
					} catch (Exception e) {
						e.printStackTrace();
						pd.dismiss();
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "短信恢复失败", 1).show();
						Looper.loop();//手动的轮循一下
					}
					
				}
			}.start();
			
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
