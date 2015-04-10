package cn.chenlin.mobilesafe.service;

import java.util.List;

import cn.chenlin.mobilesafe.domain.SmsInfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackupSmsService extends Service {

	SmsInfoService service;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		service=new SmsInfoService(this);
		//在服务里开启一个子线程来备份,里面必须重写run方法
		new Thread(){
			@Override
			public void run() {
				super.run();
				List<SmsInfo> smsInfos=service.getSmsInfo();
				//获取到短信信息后放到文件里面
			}
		}.start();
	}
	
	
	
}
