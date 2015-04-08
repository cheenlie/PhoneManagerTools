package cn.chenlin.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.db.dao.BlackNumberDAO;
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
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
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
	private BlackNumberDAO dao;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new MyPhoneListen();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		dao=new BlackNumberDAO(this);// ����ʼ���ͻ���ʹ��daoʱ������ָ��
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
				
				if(dao.find(incomingNumber)){
					endCall();
				}
				
				String address = NumberAddressService
						.getNumberAddress(incomingNumber);
				Log.i(TAG, "������λ��" + address);
				// Toast.makeText(getApplicationContext(),"������Ϊ"+address,
				// 1).show();
				showLocation(address);
				
				break;
			case TelephonyManager.CALL_STATE_IDLE: // �޺���
				if (view != null) {
					windowManager.removeView(view);
					view = null;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // ��ͨ�绰״̬
				if (view != null) {
					windowManager.removeView(view);
					view = null;
				}
				break;
			}
		}
		// �������ڲ�������ʵ��getSystemService��������������Ϊ������ʹ��service��this������Ϊ������Ͳ���service����
		// ����AddressService.thisȴ����

		private void endCall() {
			//�Ҷϵ绰������һ��ϵͳ���񷽷�����android2.0�Ժ�Ͳ���¶���û�ʹ�ã�
			//�����Ҫʹ�÷����������ϵͳServiceManager�Ķ��� ��Ȼ���ٰ��������ӳ�����
			//���Ҫʹ��AIDL
			try {
				//������getService�������
				Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
				//Ȼ��������getService��������TELEPHONY_SERVICE���񣬻�ȡ�绰�����ibinder����
				IBinder binder = (IBinder)method.invoke(null, new Object[]{TELEPHONY_SERVICE});
				//�õ����binder�����ת����interface�Ľӿ�����
				ITelephony telephony = ITelephony.Stub.asInterface(binder);
				telephony.endCall();
			} catch (Exception e) {
				e.printStackTrace(); 
			} 
		}
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
		
		//paramsĬ�����������Ҿ��еģ�������ͨ�����ó�ʼֵ���ܸı�����Ĭ��ֵ
		params.gravity=Gravity.LEFT|Gravity.TOP;  //����Ͷ���λ���յ�
		params.x=sp.getInt("lastx", 0);//�趨x���ƫ����
		params.y=sp.getInt("lasty", 0);
		

		// 4.5-1�Լ�����һ��view���������䱳����ɫ�ͷ��day3-20-[13-20minute]
		view = View.inflate(getApplicationContext(),
				cn.chenlin.mobilesafe.R.layout.show_location, null);

		// 4.5-3�ҵ��������,�������view���ϱ���
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_location);
		// �õ�background�ı�������ֵ
		int backgroundid = sp.getInt("background", 0);
		if (backgroundid == 0) {
			ll.setBackgroundResource(R.drawable.call_locate_gray);
		} else if (backgroundid == 1) {
			ll.setBackgroundResource(R.drawable.call_locate_orange);
		} else {
			ll.setBackgroundResource(R.drawable.call_locate_green);
		}

		TextView tv = (TextView) view.findViewById(R.id.tv_show_location);

		// ����һ��textview����ʾ
		// tv = new TextView(AddressService.this);
		tv.setTextSize(24);
		tv.setText(address);

		// ���ϵͳ������ʵ�ְ�textview������params��
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
