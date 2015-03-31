package cn.chenlin.mobilesafe.ui;


import cn.chenlin.mobilesafe.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetupGuide2Activity extends Activity implements OnClickListener {
	private static final String TAG = "SetupGuide2Activity";
	private Button bt_next;
	private Button bt_pre;
	private Button bt_bind;
	private CheckBox cb_bind;
	private SharedPreferences sp;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//把界面写好后，布置到逻辑中来
		setContentView(R.layout.setupguide2);
		//sp的初始化，老记不住
		sp=getSharedPreferences("config",Context.MODE_PRIVATE);
		
		Log.i(TAG,"-1监听按钮事件");
		
		bt_next=(Button)this.findViewById(R.id.bt_next);//在不同的activity里，文件名是可以重复的
		bt_pre=(Button) this.findViewById(R.id.bt_previous);
		bt_bind=(Button) this.findViewById(R.id.bt_bind);
		cb_bind=(CheckBox) this.findViewById(R.id.cb_bind);
		
		bt_next.setOnClickListener(this);
		bt_pre.setOnClickListener(this);
		bt_bind.setOnClickListener(this);
		
		Log.i(TAG,"监听按钮事件");
		
		//首先初始化checkbox的状态,查看是否已经初始化
	   String sim=sp.getString("sim",null);
	   if(sim!=null){
		   cb_bind.setText("已经绑定");
		   cb_bind.setChecked(true);
	   }else {
		   cb_bind.setText("没有绑定");
		   cb_bind.setChecked(false);
		   
		   //没有绑定，需要解除SIM卡绑定
		   resetSimInfo();
	   }
		
		cb_bind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//加入发生改变
				if(isChecked){
					   cb_bind.setText("已经绑定");//对应上面设置的true or false
					   setSimInfo();
				}else {
					   cb_bind.setText("没有绑定");
					   resetSimInfo();
				}
			}
		} );
		
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_bind:
			setSimInfo();
		
		   cb_bind.setText("已经绑定");
		   cb_bind.setChecked(true);
			
			break;

		case R.id.bt_next:
			Intent intent3= new Intent(this,SetupGuide3Activity.class);
			finish();
			startActivity(intent3);
			overridePendingTransition(R.anim.alpha_in , R.anim.alpha_out);
		
			break;
		case R.id.bt_previous:
			Intent intent= new Intent(this,SetupGuideActivity.class);
			finish();
			startActivity(intent);
			overridePendingTransition(R.anim.alpha_in , R.anim.alpha_out);
		
			break;

		}
	}


	private void setSimInfo() {
		//获取电话相关服务
		TelephonyManager	manager=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//拿到电话服务后，获取SIM卡的串号（每张SIM卡唯一）
		//Requires Permission: READ_PHONE_STATE 
		String simserial= manager.getSimSerialNumber();
		//获得一个sp的编辑器
		Editor editor= sp.edit();
		editor.putString("sim", simserial);
		//提交数据到sp文件中
		editor.commit();
	}
	
	/**
	 * 去掉SIM卡绑定
	 */
	private void resetSimInfo() {
		Editor editor=sp.edit();
		editor.putString("sim", "");
		editor.commit();
		
	}

}
