package cn.chenlin.mobilesafe.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.ContentProducer;
import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;

import cn.chenlin.mobilesafe.domain.SmsInfo;
import android.R.integer;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

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
	
	/**
	 * 还原短信
	 * @param path，备份文件的存放位置
	 */
	public void  restoreSms(String path,ProgressDialog pd) throws Exception{
		File file=new File(path);
//		try {
			ContentValues values;
			FileInputStream inputStream=new FileInputStream(file);
			XmlPullParser parser=Xml.newPullParser(); 
			parser.setInput(inputStream,"utf-8"); //file->stream->parser
			int type=parser.getEventType();
			values=null;
			int currentCount=0;
			while(type!=XmlPullParser.END_DOCUMENT){
				switch (type){
				case XmlPullParser.START_TAG:
					if("count".equals(parser.getName())){
						String count=parser.nextText();
						pd.setMax(Integer.parseInt(count));
						
					}
					if("sms".equals(parser.getName())){
						values=new ContentValues();
						//id是自增长的，所以可以不用插入
					}else if ("address".equals(parser.getName())){
						values.put("address",parser.nextText());  //逐个向后遍历
					}else if ("date".equals(parser.getName())){
						values.put("date",parser.nextText());
					}else if ("type".equals(parser.getName())){
						values.put("type",parser.nextText());
					}else if ("body".equals(parser.getName())){
						values.put("body",parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if("sms".equals(parser.getName())){
						ContentResolver resolver=context.getContentResolver();
						resolver.insert(Uri.parse("content://sms/"), values);
						values=null;
					}
					currentCount++;
					pd.setProgress(currentCount);
					break;
				}
				type=parser.next();  //必须向后移动不移动就会停在这儿
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
