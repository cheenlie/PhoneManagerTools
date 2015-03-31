package cn.chenlin.mobilesafe.ui;


import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.receiver.MyAdmin;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetupGuide4Activity extends Activity implements OnClickListener {
	private Button bt_pre;
	private Button bt_setup_finish;
	private CheckBox cb_isprotecting;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//把界面写好后，布置到逻辑中来
		setContentView(R.layout.setupguide4);

		bt_pre=(Button) findViewById(R.id.bt_pre);
		bt_setup_finish=(Button) findViewById(R.id.bt_setup_finish);
		cb_isprotecting=(CheckBox) findViewById(R.id.cb_isprotecting);
		
		
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		//初始化checkbox的状态
		boolean isprotecting= sp.getBoolean("isprotecting", false);
		if(isprotecting==false){
			cb_isprotecting.setText("正在保护中");
			cb_isprotecting.setChecked(true);
		}	
		//初始化后，绑定点击事件
		cb_isprotecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//加入发生改变
				if(isChecked){
					cb_isprotecting.setText("手机防盗保护中");
					Editor editor=sp.edit();
					editor.putBoolean("isprotecting", true);
					editor.commit();
					
				}else {
					cb_isprotecting.setText("手机没开启保护");
					Editor editor=sp.edit();
					editor.putBoolean("isprotecting", false);
					editor.commit();
				}
			}
		} );
		
		
		//绑定点击事件
		bt_pre.setOnClickListener(this);
		bt_setup_finish.setOnClickListener(this);
		cb_isprotecting.setOnClickListener(this);
		
		
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_pre:
			Intent intent3= new Intent(this,SetupGuide3Activity.class);
			finish();
			startActivity(intent3);
			overridePendingTransition(R.anim.alpha_in , R.anim.alpha_out);
			break;
		case R.id.bt_setup_finish:
			
			//如果是选择状态，那就表明处于保护状态
			if(cb_isprotecting.isChecked()){
				finish();
				//设置一个标示来判断已经开启防盗功能
				finishSetup();
			}else {
				//---方法一，有点霸道
//				Toast.makeText(this, "强烈建议开启保护", 0).show();
				//---方法二，弹出窗口让选择
				AlertDialog.Builder builder=new Builder(SetupGuide4Activity.this);
				builder.setTitle("提示");
				builder.setMessage("强烈建议开启手机防盗,是否完成设置");
				
				//把显示的匿名内部类(DialogInterface.OnClickListener)全路径写出来，只写onclicklistener不行
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						finishSetup();
					}
					
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
					
				});
				
				builder.create().show();
				return;
			}
			
			break;

		}
	}


	private void finishSetup() {
		Editor editor=sp.edit();
		editor.putBoolean("isSetupReady", true);
		editor.commit();
		
		//然后注册admin
		DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName mAdminName = new ComponentName(this, MyAdmin.class);
		if (!manager.isAdminActive(mAdminName)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			startActivity(intent);
		}
	}

}
