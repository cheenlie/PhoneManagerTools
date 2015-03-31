package cn.chenlin.mobilesafe.receiver;

import cn.chenlin.mobilesafe.ui.LostProtectedActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


//系统组件之一，必须要在xml配置文件中声明成activity,并设置监听优先级，较高的优先级
//广播接收者是没有任务栈的，而activity有任务栈的，都是在任务栈里运行
public class CallPhoneReceiver extends BroadcastReceiver {
	
	//接收到广播事件方法他就会自行onreceive这个方法
	@Override
	public void onReceive(Context context, Intent intent) {
		String number=getResultData();
		
		System.out.println("20122012");
		//如果拨打201212这个号码，就会打开监听界面
		if("20122012".equals(number)){
			Intent lostintent=new Intent(context,LostProtectedActivity.class);
			
			//要在广播接受者或服务里开启activity的时候，必须显示的指定一个参数FLAG_ACTIVITY_NEW_TASK
			//这个参数是指定激活的activity在自己的任务栈里运行
			lostintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostintent);
			//因为外拨电话指定了广播的接受者20122012，所以不能使用“ abortBroadcast(); ”来终止广播
			
			//手机卫士监听到后，就应该终止电话
			setResultData(null);
			
			/**
			 * 
			 * Q: 系统中没有抓取外拨信号的属性，不能抓到外拨信息。
			 * T: 设置手机防盗特征号码的界面，如“20122012”改成可输入的界面
			 */
		}
	}

}
