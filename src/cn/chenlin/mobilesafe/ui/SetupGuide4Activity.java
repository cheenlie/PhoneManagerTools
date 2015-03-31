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
		//�ѽ���д�ú󣬲��õ��߼�����
		setContentView(R.layout.setupguide4);

		bt_pre=(Button) findViewById(R.id.bt_pre);
		bt_setup_finish=(Button) findViewById(R.id.bt_setup_finish);
		cb_isprotecting=(CheckBox) findViewById(R.id.cb_isprotecting);
		
		
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		//��ʼ��checkbox��״̬
		boolean isprotecting= sp.getBoolean("isprotecting", false);
		if(isprotecting==false){
			cb_isprotecting.setText("���ڱ�����");
			cb_isprotecting.setChecked(true);
		}	
		//��ʼ���󣬰󶨵���¼�
		cb_isprotecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//���뷢���ı�
				if(isChecked){
					cb_isprotecting.setText("�ֻ�����������");
					Editor editor=sp.edit();
					editor.putBoolean("isprotecting", true);
					editor.commit();
					
				}else {
					cb_isprotecting.setText("�ֻ�û��������");
					Editor editor=sp.edit();
					editor.putBoolean("isprotecting", false);
					editor.commit();
				}
			}
		} );
		
		
		//�󶨵���¼�
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
			
			//�����ѡ��״̬���Ǿͱ������ڱ���״̬
			if(cb_isprotecting.isChecked()){
				finish();
				//����һ����ʾ���ж��Ѿ�������������
				finishSetup();
			}else {
				//---����һ���е�Ե�
//				Toast.makeText(this, "ǿ�ҽ��鿪������", 0).show();
				//---������������������ѡ��
				AlertDialog.Builder builder=new Builder(SetupGuide4Activity.this);
				builder.setTitle("��ʾ");
				builder.setMessage("ǿ�ҽ��鿪���ֻ�����,�Ƿ��������");
				
				//����ʾ�������ڲ���(DialogInterface.OnClickListener)ȫ·��д������ֻдonclicklistener����
				builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						finishSetup();
					}
					
				});
				builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){

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
		
		//Ȼ��ע��admin
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
