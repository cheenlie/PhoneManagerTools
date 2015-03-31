package cn.chenlin.mobilesafe.engine;

import java.io.InputStream;

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import cn.chenlin.mobilesafe.domain.UpdateInfo;

public class UpdateInfoParser {
	
	public  static UpdateInfo getUpdateInfo_url(InputStream is) throws Exception{
		
		UpdateInfo info=new UpdateInfo();
		
		//����һ��parser������
		XmlPullParser paser=Xml.newPullParser();
		paser.setInput(is, "utf-8");
		int type=paser.getEventType();//��λ���ļ���ͷ�ˡ�
		
		while(type!=XmlPullParser.END_DOCUMENT){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(paser.getName())){
					String version=paser.nextText();  //��ȡxml�ļ���Ϣ
					info.setVersion(version);
				}else if ("description".equals(paser.getName())) {
					String description=paser.nextText();
					info.setDescription(description);
				}else if ("apkurl".equals(paser.getName())) {
					String apkurl=paser.nextText();
					info.setApkurl(apkurl);
				}				
				break;
				
			default:
				break;
			}
			type=paser.next();
		}
		return info;
	}
}
