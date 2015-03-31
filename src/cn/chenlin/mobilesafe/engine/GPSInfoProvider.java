package cn.chenlin.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 保证这个类只有一个实例
 * @author Think
 *
 */
public class GPSInfoProvider {

	LocationManager manager;
	private static GPSInfoProvider mGpsInfoProvider;
	private static Context context;
	private static MyLoactionListener listener;
	//1. 私有化的构造方法
	private GPSInfoProvider(){}
	//2. 提供一个静态的方法，可以返回他们的一个实例
	//保证代码必须全部执行，而不会被打断，把方法放到同步代码块里面,既加上“synchronized”
	public static synchronized GPSInfoProvider getInstance(Context context){
		if(mGpsInfoProvider==null){
			mGpsInfoProvider=new GPSInfoProvider();
			GPSInfoProvider.context=context;
		}
		return mGpsInfoProvider;
	}
	
	
	public String getLocation(){
		//获取GPS的服务，要通过上下文才能获得服务,如构造方法中的方式获取上下文
		manager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//manager.getAllProviders(); // gps //wifi //
		String provider = getProvider(manager);
		// 注册位置的监听器 
		manager.requestLocationUpdates(provider,60000, 50, getListener());
		
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String location = sp.getString("location", "");
		return location;
	}
	
	private synchronized MyLoactionListener getListener(){
		if(listener==null){
			listener = new MyLoactionListener();
		}
		return listener;
	}
	
	
	// 停止gps监听
	public void stopGPSListener(){
		manager.removeUpdates(getListener());
	}
	

	private class MyLoactionListener implements LocationListener{

		/**
		 * 当手机位置发生改变的时候 调用的方法
		 */
		public void onLocationChanged(Location location) {
			String latitude ="latitude "+ location.getLatitude(); //weidu 
			String longtitude = "longtitude "+ location.getLongitude(); //jingdu
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("location", latitude+" - "+ longtitude);
			editor.commit(); //最后一次获取到的位置信息 存放到sharedpreference里面
		}

		/**
		 * 某一个设备的状态发生改变的时候 调用 可用->不可用  不可用->可用
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		/**
		 * 某个设备被打开
		 */
		public void onProviderEnabled(String provider) {
			
		}

		/**某个设备被禁用
		 * 
		 */
		public void onProviderDisabled(String provider) {
			
		}


		
	}
	
	/**\
	 * 
	 * @param manager 位置管理服务
	 * @return 最好的位置提供者
	 */
	private String getProvider(LocationManager manager){
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setSpeedRequired(true);
		criteria.setCostAllowed(true);
		return  manager.getBestProvider(criteria, true);
	}
	
}
