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
		// ��ȡ���ŵ�����
		// #*location*#123456
		
		//�����Ǵ���pdus�е�
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			
			//����pdus����ȡpdus��������ݣ��õ���������
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			//��ȡ��������
			String content = sms.getMessageBody();
			Log.i(TAG, "��������" + content);
			//��ȡ����Ŀ�ĵ�
			String sender = sms.getOriginatingAddress();      //??Ŀ�ĵز�����config�ļ�����
			if ("#*location*#".equals(content)) {              //������Ϊ�˱��ڲ��ԣ��Զ����ģ�����ĵ绰����
				// ��ֹ�㲥�������˶���
				abortBroadcast();
				
				Log.i(TAG, "����" + content);
				//��ֹ�㲥���GPS���ͳ�ȥ
				GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
				//��ȡ��ǰ�ֻ�λ��
				String location = provider.getLocation();
				//����һ������
				SmsManager smsmanager = SmsManager.getDefault();
				if ("".equals(location)) {

				} else {
					Log.i(TAG, "��γ��" + content);
					smsmanager.sendTextMessage(sender, null, location, null,
							null);
				}
			}else if("#*locknow*#".equals(content)){
				//ҲҪ���ض���
				abortBroadcast();
				
				//��Ҫ��ȡ��ϵͳ�ķ���,��һ��contextҪ��Context��ʾ����
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.resetPassword("123", 0);
				manager.lockNow();
			
			}else if("#*wipedata*#".equals(content)){
				//��Ҫ��ȡ��ϵͳ�ķ���,��һ��contextҪ��Context��ʾ����
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				//ҲҪ���ض���
				abortBroadcast();
				
				//���ű�������
			}else if("#*alarm*#".equals(content)){
				//����һ��������
				MediaPlayer player=MediaPlayer.create(context,R.raw.ylzs);
				//����������С
				player.setVolume(1.0f, 1.0f);
				//�Ϳ��Բ�������
				player.start();
				//ҲҪ���ض���
				abortBroadcast();
			}
			
			if(dao.find(sender)){
				//����Ǻ���������������Ǿ������������
				abortBroadcast();
			}
			
			//���Ű�������桢��Ʊ���齱�ȵȡ���ʾ����������
			if(content.contains("��Ʊ")){
				abortBroadcast();
				//todo: �Ѷ������ݴ浽���ݿ�
			}
				
		}
	}

}
