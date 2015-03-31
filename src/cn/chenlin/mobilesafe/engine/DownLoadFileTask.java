package cn.chenlin.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpConnection;

import android.R.string;
import android.app.ProgressDialog;
import android.os.Looper;
import android.util.Log;

public class DownLoadFileTask  {
	
	/**
	 * @param path     服务器文件路径
	 * @param filepath   本地文件路径 
	 * @return   本地文件对象
	 * @throws Exception
	 */
	
//	public static File getFile(String path,String filepath) throws Exception{
	//加精确进度增加一个progressdialog参数
	public static File getFile(String path,String filepath,ProgressDialog pd) throws Exception{
	
		URL url=new URL(path);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);  //设置超时时间
		//返回状态200说明返回正常
		if(conn.getResponseCode()==200){
			
			//设置进度条最大值
			int total=conn.getContentLength();
			pd.setMax(total);
			
			InputStream is=conn.getInputStream();
			File file=new File(filepath);
			//定义文件输出流
//Looper.prepare();
			FileOutputStream fos=new FileOutputStream(file);
//Looper.loop();
			//循环的从is里读数据写到fos里来
			byte[] buffer=new byte[1024];  //长度为1024
			int len=0;//读取的数据长度
			
			int process=0;
			while((len=is.read(buffer))!=-1){  //把数据读到buffer里面,当为-1时表明读到末尾了
				fos.write(buffer, 0, len);//把每次读到的数据都写到fos里
				
				//每循环一次，更新一下精度
				process+=len;
				pd.setProgress(process);
			}
			fos.flush();//把数据flush出去
			fos.close();
			is.close();// 关不关都没问题，因为URL的inputstream会释放这个链接		
			return file;
		}
		return null;		
	}
}
