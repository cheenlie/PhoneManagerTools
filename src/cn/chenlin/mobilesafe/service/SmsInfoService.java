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
	
	/**
	 * ��ԭ����
	 * @param path�������ļ��Ĵ��λ��
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
						//id���������ģ����Կ��Բ��ò���
					}else if ("address".equals(parser.getName())){
						values.put("address",parser.nextText());  //���������
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
				type=parser.next();  //��������ƶ����ƶ��ͻ�ͣ�����
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
