package cn.chenlin.mobilesafe.service;

import cn.chenlin.mobilesafe.engine.NumberAddressService;
import android.app.Service;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddressService extends Service {

	private static final String TAG = "AddressService";
	private TelephonyManager manger;
	private PhoneStateListener listener;
	private WindowManager windowManager;
	private TextView tv;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new MyPhoneListen();
		manger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manger.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
	}

	public class MyPhoneListen extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // ����״̬
				Log.i(TAG, "�������Ϊ��" + incomingNumber);
				String address = NumberAddressService
						.getNumberAddress(incomingNumber);
				Log.i(TAG, "������λ��" + address);
				// Toast.makeText(getApplicationContext(),"������Ϊ"+address,
				// 1).show();
				showLocation(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE: // �޺���
				if (tv != null) {
					windowManager.removeView(tv);
					tv = null;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // ��ͨ�绰״̬
				if (tv != null) {
					windowManager.removeView(tv);
					tv = null;
				}
				break;
			}
		}
		// �������ڲ�������ʵ��getSystemService��������������Ϊ������ʹ��service��this������Ϊ������Ͳ���service����
		// ����AddressService.thisȴ����
	}

	/**
	 * �ڴ�������ʾ��������Ϣ
	 * 
	 * @param address
	 */

	private void showLocation(String address) {
		WindowManager.LayoutParams params = new LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// ���Ƕ����һ����˾����
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		
		//�Լ�����һ��view���������䱳����ɫ�ͷ��
//		View view;
//		view=View.inflate(getApplicationContext(), BIND_AUTO_CREATE, null);
//		LinearLayout ll=view.findViewById(BIND_AUTO_CREATE);
		
		
		// ����һ��textview����ʾ
		tv = new TextView(AddressService.this);
		tv.setText("������Ϊ��" + address);

		// ���ϵͳ������ʵ�ְ�textview������params��
		// windowManager = (WindowManager)
		// this.getSystemService(WINDOW_SERVICE);
		windowManager.addView(tv, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		manger.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
}
