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
	
	//返回的是一个集合，创建一个SmsInfo这个业务bean
	public List<SmsInfo> getSmsInfo(){
		List<SmsInfo> smsInfos=new ArrayList<SmsInfo>();
		ContentResolver resolver=context.getContentResolver();
		//所有内容提供者的前缀是“content：//”，主机名是sms，在短信这个程序里面反编译出来是显示的为“android:authorities="sms"”
		//源码显示，sms直接就可以访问到到sms中各个字段的内容
		Uri uri=Uri.parse("content://sms/");
		//需要获取的内容包括“"_id","address","date","type","body"”
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
			//构造函数赋值，比geter和seter赋值要方便些
			smsInfo=new SmsInfo(id, address, date, type, body);
			smsInfos.add(smsInfo);
			smsInfo=null;			
		}
		
		return smsInfos;
	}
	
	
}
