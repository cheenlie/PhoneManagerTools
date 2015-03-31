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
	
	private static final String TAG = "SplashActivity";//ϰ�ߺ�������ͬ�����ڱ�����Ǹ����ӡ������
	public TextView tv_splash_version ;
	public LinearLayout ll_splash_main;
	private UpdateInfo info;
	private String versiontext;
	private ProgressDialog pd;
	
	//Ҫ�����̷߳�����Ϣ�ͱ��붨���handler
	private Handler handler=new  Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//�����߳̽��յ���Ϣ��Ϳ�ʼ�ж��Ƿ���Ҫ����
			//�жϷ������汾����ͻ��˰汾���Ƿ���ͬ
			if(isNeedUpdate(versiontext)){
				Log.i(TAG,"���������Ի���");
				showUpdateDialog();
			}	
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//ȡ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		//��ʼ�����ؽ�����
		pd=new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("��������");
		
		//���ð汾��Ϣ
		tv_splash_version=(TextView) this.findViewById(R.id.tv_splash_version);
		versiontext=getVersion();

		//��activity��ʱ�������ټ����£������ټ�һ���߳�
		new Thread(){
			@Override
			public void run(){
				super.run();
				try {
					sleep(2000);
					//˯�������ӣ�ֱ�ӷ��͸�����Ϣ�����
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}.start();
		
		tv_splash_version.setText("�汾��"+versiontext);
		
		//���Ӷ���Ч��
		ll_splash_main=(LinearLayout) this.findViewById(R.id.ll_splash_main);
		AlphaAnimation aa=new AlphaAnimation(0.0f,1.0f);//һ�������Ч������������
		aa.setDuration(2000);//���ó�������
		ll_splash_main.startAnimation(aa);
		
		//��ɴ���ȫ����ʾ
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/*
	 * ��ʾ�����Ի���
	 * 
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder=new Builder(this);//�½�һ�������Ի������
		builder.setIcon(R.drawable.icon5);
		builder.setTitle("�����Ի���");
		builder.setMessage(info.getDescription());
		builder.setCancelable(false);//���û�����ȡ���Ի���
	
		builder.setPositiveButton("ȷ��", new OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG,"�����ļ�apk  "+info.getApkurl());	
				
				//�ж�SD���Ƿ����
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//�ڲ��಻����ʾ//��Ϊ�������̣߳�����Ҫ��ʾ��ȥ������start����
					//DownLoadFileThreadTast task=new DownLoadFileThreadTast(info.getApkurl(),Environment.getExternalStorageDirectory().getPath()+"/new.apk");
					DownLoadFileThreadTask task=new DownLoadFileThreadTask(info.getApkurl(),"/sdcard/new.apk");
					pd.show();				
					new Thread(task).start();	
				}else {
					Toast.makeText(getApplicationContext(), "SD��������", 1).show();
					loadMainUI();
				}
			}	
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG,"�û�ȡ��������������");
				loadMainUI();
			}
		});
		builder.create().show();//���������ʾ�Ի���
	}
	
	//�����߳�	
	private class DownLoadFileThreadTask implements Runnable {
		private  String path;//������·��
		private String filepath;//�����ļ�·��
		
		public DownLoadFileThreadTask(String path,String filepath){
			this.path=path;
			this.filepath=filepath;
		}
		@Override
		public void run() { //ʵ��Runnable�ӿ�ûʵ�ֵķ���
			try {
				Log.i(TAG,"--run--");
				File file=DownLoadFileTask.getFile(path, filepath,pd);
				Log.i(TAG,"���سɹ�");
				pd.dismiss();
				
				//��װ
				install(file);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "�����ļ�ʧ��", 0).show();
				pd.dismiss();
				loadMainUI();
			}		
		}	
	}

	/*
	 * @param versiontext ��ǰ�İ汾����Ϣ
	 * @return �Ƿ���Ҫ����
	 *
	 */
	private boolean isNeedUpdate(String versiontext) {
		UpdateInfoService service=new UpdateInfoService(this);
		
		try {		
			info=service.getUpdateInfo(R.string.updateurl);

			String version=info.getVersion();
			
			if (versiontext.equals(version)) {
				Log.i(TAG, "�汾����ͬ������������������ҳ��");
				loadMainUI();
				return false;
			}else {
				Log.i(TAG, "�汾�Ų�ͬ��Ҫ����");
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "��ȡ������Ϣ�쳣",0).show();
			Log.i(TAG, "��ȡ������Ϣ�쳣��������ҳ��");
			loadMainUI();
			return false;
		}
	}

	//��ȡ�汾��
	public String getVersion(){
		try {
			
			PackageManager manager=getPackageManager();			
			PackageInfo info=manager.getPackageInfo(getPackageName(),0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "�汾��δ֪";
		}
		
	}
	
	
	/**
	 * ����������
	 */
	public void loadMainUI(){
		Intent intent=new Intent(SplashActivity.this,MainActivity.class);//����MainActivity���
		startActivity(intent);
		finish();//�ѵ�ǰActivity������ջ�Ƴ�
	}
	
	/*
	 * ��װapk
	 */
	private void install(File file){
		
		//�ҵ�ϵͳ��װAPK��������������仰
		Intent intent=new Intent();
		intent.setAction(Intent.ACTION_VIEW);             //apk��Ӧ��mime�ļ����ͣ���web������conf/web.xml�ļ���
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");//apk�ļ�����������
		
		//�رյ�ǰactivity
		finish();
		//���װactivity
		startActivity(intent);
	}

}
