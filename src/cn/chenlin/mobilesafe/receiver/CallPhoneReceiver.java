package cn.chenlin.mobilesafe.receiver;

import cn.chenlin.mobilesafe.ui.LostProtectedActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


//ϵͳ���֮һ������Ҫ��xml�����ļ���������activity,�����ü������ȼ����ϸߵ����ȼ�
//�㲥��������û������ջ�ģ���activity������ջ�ģ�����������ջ������
public class CallPhoneReceiver extends BroadcastReceiver {
	
	//���յ��㲥�¼��������ͻ�����onreceive�������
	@Override
	public void onReceive(Context context, Intent intent) {
		String number=getResultData();
		
		System.out.println("20122012");
		//�������201212������룬�ͻ�򿪼�������
		if("20122012".equals(number)){
			Intent lostintent=new Intent(context,LostProtectedActivity.class);
			
			//Ҫ�ڹ㲥�����߻�����￪��activity��ʱ�򣬱�����ʾ��ָ��һ������FLAG_ACTIVITY_NEW_TASK
			//���������ָ�������activity���Լ�������ջ������
			lostintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostintent);
			//��Ϊ�Ⲧ�绰ָ���˹㲥�Ľ�����20122012�����Բ���ʹ�á� abortBroadcast(); ������ֹ�㲥
			
			//�ֻ���ʿ�������󣬾�Ӧ����ֹ�绰
			setResultData(null);
			
			/**
			 * 
			 * Q: ϵͳ��û��ץȡ�Ⲧ�źŵ����ԣ�����ץ���Ⲧ��Ϣ��
			 * T: �����ֻ�������������Ľ��棬�硰20122012���ĳɿ�����Ľ���
			 */
		}
	}

}
