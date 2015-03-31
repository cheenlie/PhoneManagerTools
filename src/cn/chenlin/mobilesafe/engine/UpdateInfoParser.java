package cn.chenlin.mobilesafe.engine;

import java.io.InputStream;

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import cn.chenlin.mobilesafe.domain.UpdateInfo;

public class UpdateInfoParser {
	
	public  static UpdateInfo getUpdateInfo_url(InputStream is) throws Exception{
		
		UpdateInfo info=new UpdateInfo();
		
		//定义一个parser解析器
		XmlPullParser paser=Xml.newPullParser();
		paser.setInput(is, "utf-8");
		int type=paser.getEventType();//定位到文件开头了。
		
		while(type!=XmlPullParser.END_DOCUMENT){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(paser.getName())){
					String version=paser.nextText();  //获取xml文件信息
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
