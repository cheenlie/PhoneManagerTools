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
 * ��֤�����ֻ��һ��ʵ��
 * @author Think
 *
 */
public class GPSInfoProvider {

	LocationManager manager;
	private static GPSInfoProvider mGpsInfoProvider;
	private static Context context;
	private static MyLoactionListener listener;
	//1. ˽�л��Ĺ��췽��
	private GPSInfoProvider(){}
	//2. �ṩһ����̬�ķ��������Է������ǵ�һ��ʵ��
	//��֤�������ȫ��ִ�У������ᱻ��ϣ��ѷ����ŵ�ͬ�����������,�ȼ��ϡ�synchronized��
	public static synchronized GPSInfoProvider getInstance(Context context){
		if(mGpsInfoProvider==null){
			mGpsInfoProvider=new GPSInfoProvider();
			GPSInfoProvider.context=context;
		}
		return mGpsInfoProvider;
	}
	
	
	public String getLocation(){
		//��ȡGPS�ķ���Ҫͨ�������Ĳ��ܻ�÷���,�繹�췽���еķ�ʽ��ȡ������
		manager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//manager.getAllProviders(); // gps //wifi //
		String provider = getProvider(manager);
		// ע��λ�õļ����� 
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
	
	
	// ֹͣgps����
	public void stopGPSListener(){
		manager.removeUpdates(getListener());
	}
	

	private class MyLoactionListener implements LocationListener{

		/**
		 * ���ֻ�λ�÷����ı��ʱ�� ���õķ���
		 */
		public void onLocationChanged(Location location) {
			String latitude ="latitude "+ location.getLatitude(); //weidu 
			String longtitude = "longtitude "+ location.getLongitude(); //jingdu
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("location", latitude+" - "+ longtitude);
			editor.commit(); //���һ�λ�ȡ����λ����Ϣ ��ŵ�sharedpreference����
		}

		/**
		 * ĳһ���豸��״̬�����ı��ʱ�� ���� ����->������  ������->����
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		/**
		 * ĳ���豸����
		 */
		public void onProviderEnabled(String provider) {
			
		}

		/**ĳ���豸������
		 * 
		 */
		public void onProviderDisabled(String provider) {
			
		}


		
	}
	
	/**\
	 * 
	 * @param manager λ�ù������
	 * @return ��õ�λ���ṩ��
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
