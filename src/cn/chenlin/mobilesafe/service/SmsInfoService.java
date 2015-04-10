package cn.chenlin.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.ContentProducer;

import cn.chenlin.mobilesafe.domain.SmsInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsInfoService {
	private Context context;
	
	public  SmsInfoService(Context context){
		this.context=context;
	}
	
	//���ص���һ�����ϣ�����һ��SmsInfo���ҵ��bean
	public List<SmsInfo> getSmsInfo(){
		List<SmsInfo> smsInfos=new ArrayList<SmsInfo>();
		ContentResolver resolver=context.getContentResolver();
		//���������ṩ�ߵ�ǰ׺�ǡ�content��//������������sms���ڶ�������������淴�����������ʾ��Ϊ��android:authorities="sms"��
		//Դ����ʾ��smsֱ�ӾͿ��Է��ʵ���sms�и����ֶε�����
		Uri uri=Uri.parse("content://sms/");
		//��Ҫ��ȡ�����ݰ�����"_id","address","date","type","body"��
		Cursor cursor=resolver.query(uri, 
				new String[]{"_id","address","date","type","body"},
				null, null, "date desc");
		SmsInfo smsInfo;
		while(cursor.moveToNext()){
			String id=cursor.getString(0);
			String address=cursor.getString(1);
			String date=cursor.getString(2);
			int type=cursor.getInt(3);
			String body=cursor.getString(4);
			//���캯����ֵ����geter��seter��ֵҪ����Щ
			smsInfo=new SmsInfo(id, address, date, type, body);
			smsInfos.add(smsInfo);
			smsInfo=null;			
		}
		
		return smsInfos;
	}
	
	
}
