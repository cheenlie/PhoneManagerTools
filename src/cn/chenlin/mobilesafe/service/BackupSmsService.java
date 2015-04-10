package cn.chenlin.mobilesafe.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import cn.chenlin.mobilesafe.domain.SmsInfo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

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
				//super.run(); 
				try {
					List<SmsInfo> smsInfos=service.getSmsInfo();
					//��ȡ��������Ϣ��ŵ��ļ�����
					//�½�һ���ļ�
					File file=new File("/sdcard/smsbackup.xml");
					//�½�һ�����л���
					XmlSerializer serializer=Xml.newSerializer();
					//����һ�������,�����ָ���ļ�
					FileOutputStream os=new FileOutputStream(file);
					serializer.setOutput(os,"utf-8");//���л������os��
					serializer.startDocument("utf-8", true);
					serializer.startTag(null,"smss");
					serializer.startTag(null, "count");
					serializer.text(smsInfos.size()+"");
					serializer.endTag(null, "count");
					for(SmsInfo info : smsInfos){
						serializer.startTag(null,"sms");
						
						serializer.startTag(null, "id");
						serializer.text(info.getId());
						serializer.endTag(null, "id");
						
						serializer.startTag(null, "address");
						serializer.text(info.getAddress());
						serializer.endTag(null, "address");
						
						serializer.startTag(null, "date");
						serializer.text(info.getDate());
						serializer.endTag(null, "date");
						
						serializer.startTag(null, "type");
						serializer.text(info.getType()+"");//type��int�ͣ���һ�������ͱ����string��
						serializer.endTag(null, "type");
						
						serializer.startTag(null, "body");
						serializer.text(info.getBody());
						serializer.endTag(null, "body");
						
						serializer.endTag(null,"sms");
					}
					System.out.println("here");
					serializer.endTag(null, "smss");
					serializer.endDocument();
					//���ļ��������������ύ��ȥ
					os.flush();
					os.close();
					System.out.println("true");
					//���߳�����Ĭ�ϲ�����ʾ��˾�ģ���Ϊ���߳�����û��looper��handler����Ϣ����
					//���ǿ����½�һ��looper
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "���ű������", 1).show();
					Looper.loop();//�ֶ�����ѭһ��
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("false");
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "���ű��ݳ���", 1).show();
					Looper.loop();//�ֶ�����ѭһ��
				}
			}
		}.start();
	}
}
