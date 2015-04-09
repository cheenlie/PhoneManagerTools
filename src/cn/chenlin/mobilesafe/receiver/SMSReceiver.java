package cn.chenlin.mobilesafe.receiver;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.db.dao.BlackNumberDAO;
import cn.chenlin.mobilesafe.engine.GPSInfoProvider;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;



public class SMSReceiver extends BroadcastReceiver {

	private BlackNumberDAO dao;
	private static final String TAG = "SMSReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		dao=new BlackNumberDAO(context);
		// 获取短信的内容
		// #*location*#123456
		
		//短信是存在pdus中的
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			
			//遍历pdus，获取pdus里面的数据，得到短信内容
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			//获取短信正文
			String content = sms.getMessageBody();
			Log.i(TAG, "短信内容" + content);
			//获取发送目的地
			String sender = sms.getOriginatingAddress();      //??目的地不是在config文件中吗？
			if ("#*location*#".equals(content)) {              //答：这是为了便于测试，自动检测模拟器的电话号码
				// 终止广播，拦截了短信
				abortBroadcast();
				
				Log.i(TAG, "发送" + content);
				//终止广播后把GPS发送出去
				GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
				//获取当前手机位置
				String location = provider.getLocation();
				//够造一条短信
				SmsManager smsmanager = SmsManager.getDefault();
				if ("".equals(location)) {

				} else {
					Log.i(TAG, "经纬度" + content);
					smsmanager.sendTextMessage(sender, null, location, null,
							null);
				}
			}else if("#*locknow*#".equals(content)){
				//也要拦截短信
				abortBroadcast();
				
				//先要获取到系统的服务,后一个context要用Context表示常量
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.resetPassword("123", 0);
				manager.lockNow();
			
			}else if("#*wipedata*#".equals(content)){
				//先要获取到系统的服务,后一个context要用Context表示常量
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				//也要拦截短信
				abortBroadcast();
				
				//播放报警音乐
			}else if("#*alarm*#".equals(content)){
				//定义一个播放器
				MediaPlayer player=MediaPlayer.create(context,R.raw.ylzs);
				//设置声音大小
				player.setVolume(1.0f, 1.0f);
				//就可以播放音乐
				player.start();
				//也要拦截短信
				abortBroadcast();
			}
			
			if(dao.find(sender)){
				//如果是黑名单里面的内容那就拦截这个短信
				abortBroadcast();
			}
			
			//短信包含“广告、发票、抽奖等等”表示黑名单短信
			if(content.contains("发票")){
				abortBroadcast();
				//todo: 把短信内容存到数据库
			}
				
		}
	}

}
