package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.util.MD5Encoder;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LostProtectedActivity extends Activity implements OnClickListener {
	private static final String TAG = "LostProtectedActivity";
	private SharedPreferences sp;
	private Dialog dialog;
	private Button bt_ok;
	private Button bt_cancel;
	private Button bt_normal_ok;
	private Button bt_normal_cancel;
	private EditText  et_pwd;
	private EditText  et_confirm;
	private TextView tv_lost_protected_number;
	private TextView tv_reentry_setup_guide;
	private CheckBox cb_isprotecting;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		
		//判断用户是否输入密码
		Log.i(TAG,"进入防盗界面");
		if(isPWDSetup()){
			//进入创建密码界面
			Log.i(TAG,"设置了密码，登陆对话框");
			showNormalEntryDialog();
		}else {
			Log.i(TAG,"没正常设置了密码，设置秘密对话框");
			//第一次进入程序时的对话框
			showFirstEntryDialog();
		}
	}
	
	/**
	 * 正常登陆对话框
	 * 
	 */
	private void showNormalEntryDialog() {
		//更改系统默认对话框的tittle，background,在新建的style中建立的继承系统自带样式
				dialog=new Dialog(this,R.style.MyDialog);
				View view=View.inflate(this, R.layout.normal_entry_dialog, null);
				et_pwd =(EditText) view.findViewById(R.id.et_normal_entry_pwd);
				bt_normal_ok =(Button) view.findViewById(R.id.bt_normal_dialog_ok);
				bt_normal_cancel=(Button) view.findViewById(R.id.bt_normal_dialog_cancel);
				
				Log.i(TAG,"进入登陆窗口窗口");
				bt_normal_ok.setOnClickListener(this); //监听当前窗口的按钮的点击事件
				bt_normal_cancel.setOnClickListener(this);
				dialog.setContentView(view);
				dialog.show();		
	}

	/**
	 * 程序第一次进入时的对话框
	 */
	private void showFirstEntryDialog() {

		//更改系统默认对话框的tittle，background,在新建的style中建立的继承系统自带样式
		dialog=new Dialog(this,R.style.MyDialog);
		
		//显示什么内容呢？需要把布局创建出来
//		dialog.setContentView(R.layout.first_entry_dialog);//这种方法不能处理界面里面的按钮等空间，从而采用以下办法
		
		//办法为：先把xml转换成view对象
		View view=View.inflate(this, R.layout.first_entry_dialog, null);
		et_pwd =	(EditText) view.findViewById(R.id.et_first_entry_pwd);
		et_confirm=(EditText) view.findViewById(R.id.et_first_entry_pwd_confirm);
		bt_ok = (Button) view.findViewById(R.id.bt_first_dialog_ok);
		bt_cancel= (Button) view.findViewById(R.id.bt_first_dialog_cancel);
		
		Log.i(TAG,"进入点击窗口");
		
		bt_ok.setOnClickListener(this);//监听当前窗口的按钮的点击事件
		bt_cancel.setOnClickListener(this);
		dialog.setContentView(view);
		//建好dialog后就可以show出来啦
		dialog.show();
		
	}

	/**
	 * 检查sharepreferences是否有密码设置
	 * @return
	 */
	private boolean isPWDSetup(){
		String password=sp.getString("password", null);
		if(password==null){
			return false;
		}else {
			if("".equals(password)){
				return false;
			}else {
				return true;
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_first_dialog_cancel:  //如果是取消按钮
			
			dialog.dismiss();
			
			//跳到主界面
			finish();
			Intent intent_gobacktoMainAcitivity1=new Intent(LostProtectedActivity.this,MainActivity.class);
			startActivity(intent_gobacktoMainAcitivity1);
			
			break;
			
		case R.id.bt_first_dialog_ok:  //如果是确定按钮
			String pwd=et_pwd.getText().toString().trim();
			String pwd_confirm=et_confirm.getText().toString().trim();
			
			if("".equals(pwd)||"".equals(pwd_confirm)){
				Toast.makeText(getApplicationContext(), "密码不能为空~", 0).show();
				return;
			}else {
				if(pwd.equals(pwd_confirm)){  //假如相等就拿到密码了
					Editor editor= sp.edit();
					
					//把秘密转换成密文保存
					String md5_pwd=MD5Encoder.enCode(pwd);
					editor.putString("password",md5_pwd );	
					//一定要提交
					editor.commit();
					
					
					//提交后到设置界面
					finish();
					Intent intent=new Intent(LostProtectedActivity.this,SetupGuideActivity.class);
					startActivity(intent);
					
				}else {
					Toast.makeText(getApplicationContext(), "两次密码不同", 0).show();
					return;
				}
			}
			//自己创立的窗口一定要dismiss
			dialog.dismiss();
			break;
		case R.id.bt_normal_dialog_ok:  //如果是确定按钮
			String normal_pwd=et_pwd.getText().toString().trim();
			if("".equals(normal_pwd)){
				Toast.makeText(getApplicationContext(), "请输入密码", 0).show();
				return;
			}else {
				String  realPWD=sp.getString("password", "");
				if(realPWD.equals(MD5Encoder.enCode(normal_pwd))){
					if(isSetUp()){
						//进入主界面
						Log.i(TAG, "加载手机防盗主界面");
						
						//加载页面和其他方式不一样呢
						setContentView(R.layout.lost_protected);
						
						//设置完contentview就能寻找其中的控件了
						 tv_lost_protected_number=(TextView) findViewById(R.id.tv_lost_protected_number);
						 tv_reentry_setup_guide=(TextView) findViewById(R.id.tv_reentry_setup_guide);
						 cb_isprotecting=(CheckBox) findViewById(R.id.cb_isprotecting);		
						 
						String number=sp.getString("safenumber", "");
						tv_lost_protected_number.setText("手机安全号码为 :"+number);
						
						//确定一个点击事件,重新进入向导界面
						tv_reentry_setup_guide.setOnClickListener(this);
						
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
						
						
					}else {
						
						Log.i(TAG, "激活设置向导界面");
						
						//把当前activity finish掉
						finish();
						Intent intent=new Intent(LostProtectedActivity.this,SetupGuideActivity.class);
						startActivity(intent);
					}
					
				}else {
					Toast.makeText(getApplicationContext(), "输入密码不正确", 0).show();
					return ;
				}
			}
			dialog.dismiss();
			break;
			
		case R.id.bt_normal_dialog_cancel:  //如果是取消按钮
			dialog.dismiss();
			
			//跳到主界面
			finish();
			Intent intent_gobacktoMainAcitivity2=new Intent(LostProtectedActivity.this,MainActivity.class);
			startActivity(intent_gobacktoMainAcitivity2);
			
			break;
			
		case R.id.tv_reentry_setup_guide:  //如果是进入向导按钮
			finish();
			Intent intent=new Intent(LostProtectedActivity.this,SetupGuideActivity.class);
			startActivity(intent);
		}
	}
	
	/**
	 * 判断用户是否已经设置用户向导
	 * @return
	 */
	private boolean isSetUp(){
		boolean issetup=sp.getBoolean("isSetupReady", false);//如果没有值就返回false这个值
		return issetup;
	} 
}
