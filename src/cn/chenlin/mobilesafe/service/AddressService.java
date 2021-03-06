package cn.chenlin.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.db.dao.BlackNumberDAO;
import cn.chenlin.mobilesafe.engine.NumberAddressService;
import cn.chenlin.mobilesafe.ui.CallSmsActivity;
import android.R.color;
import android.R.drawable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
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
	private long starttime;
	private long endtime;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new MyPhoneListener();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		dao=new BlackNumberDAO(this);// 不初始化就会在使用dao时弹出空指针
		manger = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manger.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
	}

	public class MyPhoneListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				Log.i(TAG, "来电号码为：" + incomingNumber);
				
				starttime=System.currentTimeMillis();
				
				if(dao.find(incomingNumber)){
					endCall();
					
					//创建记录不是马上就完成的，所以删除记录的动作需要和创建记录同步
					//第一种：延迟几秒删除，用户体验不好；第二种：用内容观察者方式来实现
					//对于第二种方法先注册一个内容观察者，观察calllog里面的URI是否发生改变
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(), incomingNumber));
				
				}
				
				String address = NumberAddressService
						.getNumberAddress(incomingNumber);
				Log.i(TAG, "归属地位：" + address);
				// Toast.makeText(getApplicationContext(),"归属地为"+address,
				// 1).show();
				showLocation(address);
				
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 无呼叫
				
				endtime=System.currentTimeMillis();
				if(starttime<endtime && endtime-starttime<2000)
				{
					Log.i(TAG,incomingNumber+"是一个响一声电话");
					//弹出出notification通知用户这是一个骚扰电话
					showNotification(incomingNumber);
				}
				
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
		private void endCall() {
			//挂断电话方法是一个系统服务方法，从android2.0以后就不暴露给用户使用，
			//因此需要使用反射来来获得系统ServiceManager的对象 ，然后再把这个方法映射出来
			//因此要使用AIDL
			try {
				//反射获得getService这个方法
				Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
				//然后调用这个getService方法启动TELEPHONY_SERVICE服务，获取电话管理的ibinder对象
				IBinder binder = (IBinder)method.invoke(null, new Object[]{TELEPHONY_SERVICE});
				//拿到这个binder对象后转换成interface的接口类型
				ITelephony telephony = ITelephony.Stub.asInterface(binder);
				telephony.endCall();
			} catch (Exception e) {
				e.printStackTrace(); 
			} 
		}
	}

	/**
	 * 弹出notification通知用户添加黑名单
	 * @param incomingNumber
	 */
	private void showNotification(String incomingNumber) {
		//1. 获取notification的管理服务
				NotificationManager  manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				//2 把一个要想显示的notification 对象创建出来
				int icon =R.drawable.notification;
				CharSequence tickerText = "发现响一声号码";
				long when = System.currentTimeMillis();

				Notification notification = new Notification(icon, tickerText, when);
				// 3 .配置notification的一些参数
				Context context = getApplicationContext();
				CharSequence contentTitle = "响一声号码";
				CharSequence contentText = incomingNumber;
				//点击一下这个notification就会消失
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				
				Intent notificationIntent = new Intent(this, CallSmsActivity.class);
				// 把响一声的号码 设置到intent对象里面
				notificationIntent.putExtra("number", incomingNumber);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				
				// 4. 通过manger把notification 激活
				manager.notify(0, notification);
	}
	/**
	 * 删除通话记录日志
	 * @param incomingNumber
	 */
	private void deleteCallLog(String incomingNumber) {
		ContentResolver resolver=getContentResolver();
		Cursor cursor=resolver.query(CallLog.Calls.CONTENT_URI, null, "number=?", new String[]{incomingNumber}, null);
		if(cursor.moveToNext()){  //在呼叫记录中找到了这个号码
			String id=cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id});
		}
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
		
		//params默认是上下左右居中的，那我们通过设置初始值就能改变他的默认值
		params.gravity=Gravity.LEFT|Gravity.TOP;  //以左和顶部位参照点
		params.x=sp.getInt("lastx", 0);//设定x轴的偏移量
		params.y=sp.getInt("lasty", 0);
		

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
	
	/**
	 * 定义了一个观察者必须重写onChange方法，以便捕获什么时候发生改变
	 * @author Administrator
	 *
	 */
	public class MyObserver extends ContentObserver{

		//把变化的电话号码也传进来
		private  String incomingnumber;
		
		public MyObserver(Handler handler,String incomingnumber) {
			super(handler);
			this.incomingnumber=incomingnumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			//发生改变后再删除通话记录
			deleteCallLog(incomingnumber);
			
			//contentObserver用的是回调，用完后应该取消，会耗费资源
			getContentResolver().unregisterContentObserver(this);
		}
	}
}
