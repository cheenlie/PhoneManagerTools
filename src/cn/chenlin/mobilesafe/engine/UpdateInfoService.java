package cn.chenlin.mobilesafe.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import cn.chenlin.mobilesafe.domain.*;
import cn.chenlin.mobilesafe.R;


public class UpdateInfoService {
	private Context context;  //要获取资源文件，必须要有应用程序的上下文引用
	
	//别人引用上下文的时候，必须初始化上下文
	public UpdateInfoService(Context context) {
		super();
		this.context = context;
	}

	/*
	 * @param urlid： 服务器路径string对应的id
	 * @return 文件更新信息 
	 * 
	 */
									//业务方法里面的异常用throws来处理，让调用者去处理异常
									//try catch捕获异常了，但已经扑向异常了，所以此处不用try catch
	public UpdateInfo getUpdateInfo(int urlid) throws Exception{
		String path= context.getResources().getString(urlid); 
		//必须把path包装成一个对象
		URL url=new URL(path);
		HttpURLConnection conn=  (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		InputStream is=conn.getInputStream();		
		
		return UpdateInfoParser.getUpdateInfo_url(is);	
	}
	
	

}
