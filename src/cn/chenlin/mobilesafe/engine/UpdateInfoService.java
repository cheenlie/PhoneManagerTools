package cn.chenlin.mobilesafe.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import cn.chenlin.mobilesafe.domain.*;
import cn.chenlin.mobilesafe.R;


public class UpdateInfoService {
	private Context context;  //Ҫ��ȡ��Դ�ļ�������Ҫ��Ӧ�ó��������������
	
	//�������������ĵ�ʱ�򣬱����ʼ��������
	public UpdateInfoService(Context context) {
		super();
		this.context = context;
	}

	/*
	 * @param urlid�� ������·��string��Ӧ��id
	 * @return �ļ�������Ϣ 
	 * 
	 */
									//ҵ�񷽷�������쳣��throws�������õ�����ȥ�����쳣
									//try catch�����쳣�ˣ����Ѿ������쳣�ˣ����Դ˴�����try catch
	public UpdateInfo getUpdateInfo(int urlid) throws Exception{
		String path= context.getResources().getString(urlid); 
		//�����path��װ��һ������
		URL url=new URL(path);
		HttpURLConnection conn=  (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		InputStream is=conn.getInputStream();		
		
		return UpdateInfoParser.getUpdateInfo_url(is);	
	}
	
	

}
