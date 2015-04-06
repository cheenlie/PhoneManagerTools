package cn.chenlin.mobilesafe.service;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.NumberAddressService;
import android.R.color;
import android.R.drawable;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
	private View view;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new MyPhoneListen();
		sp = getSharedPreferences("config", MODE_PRIVATE);
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
				if (view != null) {
					windowManager.removeView(view);
					view = null;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通电话状态
				if (view != null) {
					windowManager.removeView(view);
					view = null;
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

		// 4.5-1自己设置一个view，并调整其背景颜色和风格。day3-20-[13-20minute]
		view = View.inflate(getApplicationContext(),
				cn.chenlin.mobilesafe.R.layout.show_location, null);

		// 4.5-3找到这个背景,并给这个view配上背景
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_location);
		// 拿到background的背景设置值
		int backgroundid = sp.getInt("background", 0);
		if (backgroundid == 0) {
			ll.setBackgroundResource(R.drawable.call_locate_gray);
		} else if (backgroundid == 1) {
			ll.setBackgroundResource(R.drawable.call_locate_orange);
		} else {
			ll.setBackgroundResource(R.drawable.call_locate_green);
		}

		TextView tv = (TextView) view.findViewById(R.id.tv_show_location);

		// 设置一个textview来显示
		// tv = new TextView(AddressService.this);
		tv.setTextSize(24);
		tv.setText(address);

		// 获得系统对象来实现把textview挂载在params上
		// windowManager = (WindowManager)
		// this.getSystemService(WINDOW_SERVICE);
		windowManager.addView(view, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		manger.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
}
