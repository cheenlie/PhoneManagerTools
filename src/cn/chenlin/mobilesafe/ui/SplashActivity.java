package cn.chenlin.mobilesafe.ui;

import java.io.File;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.domain.UpdateInfo;
import cn.chenlin.mobilesafe.engine.DownLoadFileTask;
import cn.chenlin.mobilesafe.engine.UpdateInfoService;
import cn.chenlin.mobilesafe.ui.*;
import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	private static final String TAG = "SplashActivity";//习惯和类名相同，便于辨别是那个类打印出来的
	public TextView tv_splash_version ;
	public LinearLayout ll_splash_main;
	private UpdateInfo info;
	private String versiontext;
	private ProgressDialog pd;
	
	//要给主线程发送消息就必须定义个handler
	private Handler handler=new  Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//当主线程接收到消息后就开始判断是否需要升级
			//判断服务器版本号与客户端版本号是否相同
			if(isNeedUpdate(versiontext)){
				Log.i(TAG,"弹出升级对话框");
				showUpdateDialog();
			}	
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		//初始化下载进度条
		pd=new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("正在下载");
		
		//设置版本信息
		tv_splash_version=(TextView) this.findViewById(R.id.tv_splash_version);
		versiontext=getVersion();

		//让activity延时两秒钟再检查更新，必须再加一个线程
		new Thread(){
			@Override
			public void run(){
				super.run();
				try {
					sleep(2000);
					//睡眠两秒钟，直接发送个空消息便可以
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}.start();
		
		tv_splash_version.setText("版本号"+versiontext);
		
		//增加动画效果
		ll_splash_main=(LinearLayout) this.findViewById(R.id.ll_splash_main);
		AlphaAnimation aa=new AlphaAnimation(0.0f,1.0f);//一个渐变的效果（暗到亮）
		aa.setDuration(2000);//设置持续两秒
		ll_splash_main.startAnimation(aa);
		
		//完成窗体全屏显示
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/*
	 * 显示升级对话框
	 * 
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder=new Builder(this);//新建一个弹出对话框界面
		builder.setIcon(R.drawable.icon5);
		builder.setTitle("升级对话框");
		builder.setMessage(info.getDescription());
		builder.setCancelable(false);//让用户不能取消对话框
	
		builder.setPositiveButton("确定", new OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG,"下载文件apk  "+info.getApkurl());	
				
				//判断SD卡是否可用
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//内部类不会提示//因为他是子线程，所以要显示的去调他的start方法
					//DownLoadFileThreadTast task=new DownLoadFileThreadTast(info.getApkurl(),Environment.getExternalStorageDirectory().getPath()+"/new.apk");
					DownLoadFileThreadTask task=new DownLoadFileThreadTask(info.getApkurl(),"/sdcard/new.apk");
					pd.show();				
					new Thread(task).start();	
				}else {
					Toast.makeText(getApplicationContext(), "SD卡不可用", 1).show();
					loadMainUI();
				}
			}	
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG,"用户取消，进入主界面");
				loadMainUI();
			}
		});
		builder.create().show();//创建完毕显示对话框
	}
	
	//下载线程	
	private class DownLoadFileThreadTask implements Runnable {
		private  String path;//服务器路径
		private String filepath;//本地文件路径
		
		public DownLoadFileThreadTask(String path,String filepath){
			this.path=path;
			this.filepath=filepath;
		}
		@Override
		public void run() { //实现Runnable接口没实现的方法
			try {
				Log.i(TAG,"--run--");
				File file=DownLoadFileTask.getFile(path, filepath,pd);
				Log.i(TAG,"下载成功");
				pd.dismiss();
				
				//安装
				install(file);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "下载文件失败", 0).show();
				pd.dismiss();
				loadMainUI();
			}		
		}	
	}

	/*
	 * @param versiontext 当前的版本号信息
	 * @return 是否需要更新
	 *
	 */
	private boolean isNeedUpdate(String versiontext) {
		UpdateInfoService service=new UpdateInfoService(this);
		
		try {		
			info=service.getUpdateInfo(R.string.updateurl);

			String version=info.getVersion();
			
			if (versiontext.equals(version)) {
				Log.i(TAG, "版本号相同，无需升级，进入主页面");
				loadMainUI();
				return false;
			}else {
				Log.i(TAG, "版本号不同需要更新");
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "获取更新信息异常",0).show();
			Log.i(TAG, "获取更新信息异常，进入主页面");
			loadMainUI();
			return false;
		}
	}

	//获取版本号
	public String getVersion(){
		try {
			
			PackageManager manager=getPackageManager();			
			PackageInfo info=manager.getPackageInfo(getPackageName(),0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号未知";
		}
		
	}
	
	
	/**
	 * 调用主界面
	 */
	public void loadMainUI(){
		Intent intent=new Intent(SplashActivity.this,MainActivity.class);//激活MainActivity组件
		startActivity(intent);
		finish();//把当前Activity从任务栈移除
	}
	
	/*
	 * 安装apk
	 */
	private void install(File file){
		
		//找到系统安装APK的组件，以下三句话
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_VIEW);             //apk对应的mime文件类型，在web服务器conf/web.xml文件中
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");//apk文件的数据类型
		
		//关闭当前activity
		finish();
		//激活安装activity
		startActivity(intent);
	}

}
