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
		//在服务里开启一个子线程来备份,里面必须重写run方法
		new Thread(){
			@Override
			public void run() {
				//super.run(); 
				try {
					List<SmsInfo> smsInfos=service.getSmsInfo();
					//获取到短信信息后放到文件里面
					//新建一个文件
					File file=new File("/sdcard/smsbackup.xml");
					//新建一个序列化器
					XmlSerializer serializer=Xml.newSerializer();
					//定义一个输出流,输出到指定文件
					FileOutputStream os=new FileOutputStream(file);
					serializer.setOutput(os,"utf-8");//序列化输出到os中
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
						serializer.text(info.getType()+"");//type是int型，加一个“”就变成了string型
						serializer.endTag(null, "type");
						
						serializer.startTag(null, "body");
						serializer.text(info.getBody());
						serializer.endTag(null, "body");
						
						serializer.endTag(null,"sms");
					}
					System.out.println("here");
					serializer.endTag(null, "smss");
					serializer.endDocument();
					//把文件缓冲区的数据提交出去
					os.flush();
					os.close();
					System.out.println("true");
					//子线程里面默认不能显示土司的，因为子线程里面没有looper，handler，消息队列
					//但是可以新建一个looper
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "短信备份完成", 1).show();
					Looper.loop();//手动的轮循一下
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("false");
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "短信备份出错", 1).show();
					Looper.loop();//手动的轮循一下
				}
			}
		}.start();
	}
}
