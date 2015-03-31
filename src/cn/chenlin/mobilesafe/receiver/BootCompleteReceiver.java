package cn.chenlin.mobilesafe.receiver;

import android.R.string;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode.Mode;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";
	private  SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		//�ж��ֻ��Ƿ��ڱ���״̬
		//���������Ϣ
		sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
		Log.i(TAG,"�����ֻ�");
		//��������ˣ����ǲŽ��뱣���Ĵ���ģʽ
		 boolean isprotecting=sp.getBoolean("isprotecting", false);
		 
		 if(isprotecting){
			String sim=sp.getString("sim", "");
			
			//���sim�Ĵ���
			TelephonyManager	manager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String simserial= manager.getSimSerialNumber();
			
			String sim_serial=sp.getString("sim", "");
			
			if(!sim_serial.equals(simserial)){
				//���ͱ�������
				//��ȡ���ŷ�����
				
				Log.i(TAG,"���ͱ�������");
				SmsManager smsmanager =SmsManager.getDefault();
				String destinationnumber=sp.getString("safenumber", "");
				
				smsmanager.sendTextMessage(destinationnumber, null, "sim�����ı䣬�ֻ����ܱ���", null, null);
			}
		 }
	}

}
