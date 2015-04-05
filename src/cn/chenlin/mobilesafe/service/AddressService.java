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
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				Log.i(TAG, "来电号码为：" + incomingNumber);
				String address = NumberAddressService
						.getNumberAddress(incomingNumber);
				Log.i(TAG, "归属地位：" + address);
				// Toast.makeText(getApplicationContext(),"归属地为"+address,
				// 1).show();
				showLocation(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 无呼叫
				if (tv != null) {
					windowManager.removeView(tv);
					tv = null;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通电话状态
				if (tv != null) {
					windowManager.removeView(tv);
					tv = null;
				}
				break;
			}
		}
		// 不能再内部类里面实现getSystemService函数，好像是因为它不能使用service的this对象，因为它本身就不是service对象
		// 但是AddressService.this却可以
	}

	/**
	 * 在窗体上显示归属地信息
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
		// 这是定义的一个土司动画
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		
		//自己设置一个view，并调整其背景颜色和风格。
//		View view;
//		view=View.inflate(getApplicationContext(), BIND_AUTO_CREATE, null);
//		LinearLayout ll=view.findViewById(BIND_AUTO_CREATE);
		
		
		// 设置一个textview来显示
		tv = new TextView(AddressService.this);
		tv.setText("归属地为：" + address);

		// 获得系统对象来实现把textview挂载在params上
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
