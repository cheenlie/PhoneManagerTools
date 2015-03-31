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
	 * @param path     �������ļ�·��
	 * @param filepath   �����ļ�·�� 
	 * @return   �����ļ�����
	 * @throws Exception
	 */
	
//	public static File getFile(String path,String filepath) throws Exception{
	//�Ӿ�ȷ��������һ��progressdialog����
	public static File getFile(String path,String filepath,ProgressDialog pd) throws Exception{
	
		URL url=new URL(path);
		HttpURLConnection conn=(HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);  //���ó�ʱʱ��
		//����״̬200˵����������
		if(conn.getResponseCode()==200){
			
			//���ý��������ֵ
			int total=conn.getContentLength();
			pd.setMax(total);
			
			InputStream is=conn.getInputStream();
			File file=new File(filepath);
			//�����ļ������
//Looper.prepare();
			FileOutputStream fos=new FileOutputStream(file);
//Looper.loop();
			//ѭ���Ĵ�is�������д��fos����
			byte[] buffer=new byte[1024];  //����Ϊ1024
			int len=0;//��ȡ�����ݳ���
			
			int process=0;
			while((len=is.read(buffer))!=-1){  //�����ݶ���buffer����,��Ϊ-1ʱ��������ĩβ��
				fos.write(buffer, 0, len);//��ÿ�ζ��������ݶ�д��fos��
				
				//ÿѭ��һ�Σ�����һ�¾���
				process+=len;
				pd.setProgress(process);
			}
			fos.flush();//������flush��ȥ
			fos.close();
			is.close();// �ز��ض�û���⣬��ΪURL��inputstream���ͷ��������		
			return file;
		}
		return null;		
	}
}
