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
		//�ѽ���д�ú󣬲��õ��߼�����
		setContentView(R.layout.setupguide2);
		//sp�ĳ�ʼ�����ϼǲ�ס
		sp=getSharedPreferences("config",Context.MODE_PRIVATE);
		
		Log.i(TAG,"-1������ť�¼�");
		
		bt_next=(Button)this.findViewById(R.id.bt_next);//�ڲ�ͬ��activity��ļ����ǿ����ظ���
		bt_pre=(Button) this.findViewById(R.id.bt_previous);
		bt_bind=(Button) this.findViewById(R.id.bt_bind);
		cb_bind=(CheckBox) this.findViewById(R.id.cb_bind);
		
		bt_next.setOnClickListener(this);
		bt_pre.setOnClickListener(this);
		bt_bind.setOnClickListener(this);
		
		Log.i(TAG,"������ť�¼�");
		
		//���ȳ�ʼ��checkbox��״̬,�鿴�Ƿ��Ѿ���ʼ��
	   String sim=sp.getString("sim",null);
	   if(sim!=null){
		   cb_bind.setText("�Ѿ���");
		   cb_bind.setChecked(true);
	   }else {
		   cb_bind.setText("û�а�");
		   cb_bind.setChecked(false);
		   
		   //û�а󶨣���Ҫ���SIM����
		   resetSimInfo();
	   }
		
		cb_bind.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//���뷢���ı�
				if(isChecked){
					   cb_bind.setText("�Ѿ���");//��Ӧ�������õ�true or false
					   setSimInfo();
				}else {
					   cb_bind.setText("û�а�");
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
		
		   cb_bind.setText("�Ѿ���");
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
		//��ȡ�绰��ط���
		TelephonyManager	manager=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//�õ��绰����󣬻�ȡSIM���Ĵ��ţ�ÿ��SIM��Ψһ��
		//Requires Permission: READ_PHONE_STATE 
		String simserial= manager.getSimSerialNumber();
		//���һ��sp�ı༭��
		Editor editor= sp.edit();
		editor.putString("sim", simserial);
		//�ύ���ݵ�sp�ļ���
		editor.commit();
	}
	
	/**
	 * ȥ��SIM����
	 */
	private void resetSimInfo() {
		Editor editor=sp.edit();
		editor.putString("sim", "");
		editor.commit();
		
	}

}
