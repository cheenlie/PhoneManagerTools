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
		//判断手机是否处于保护状态
		//获得配置信息
		sp=context.getSharedPreferences("config", context.MODE_PRIVATE);
		Log.i(TAG,"重启手机");
		//如果保护了，我们才进入保护的处理模式
		 boolean isprotecting=sp.getBoolean("isprotecting", false);
		 
		 if(isprotecting){
			String sim=sp.getString("sim", "");
			
			//获得sim的串号
			TelephonyManager	manager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String simserial= manager.getSimSerialNumber();
			
			String sim_serial=sp.getString("sim", "");
			
			if(!sim_serial.equals(simserial)){
				//发送报警短信
				//获取短信发送器
				
				Log.i(TAG,"发送报警短信");
				SmsManager smsmanager =SmsManager.getDefault();
				String destinationnumber=sp.getString("safenumber", "");
				
				smsmanager.sendTextMessage(destinationnumber, null, "sim发生改变，手机可能被盗", null, null);
			}
		 }
	}

}
