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
		//�ڷ����￪��һ�����߳�������,���������дrun����
		new Thread(){
			@Override
			public void run() {
				super.run();
				List<SmsInfo> smsInfos=service.getSmsInfo();
				//��ȡ��������Ϣ��ŵ��ļ�����
			}
		}.start();
	}
	
	
	
}
