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
		
		//�ж��û��Ƿ���������
		Log.i(TAG,"�����������");
		if(isPWDSetup()){
			//���봴���������
			Log.i(TAG,"���������룬��½�Ի���");
			showNormalEntryDialog();
		}else {
			Log.i(TAG,"û�������������룬�������ܶԻ���");
			//��һ�ν������ʱ�ĶԻ���
			showFirstEntryDialog();
		}
	}
	
	/**
	 * ������½�Ի���
	 * 
	 */
	private void showNormalEntryDialog() {
		//����ϵͳĬ�϶Ի����tittle��background,���½���style�н����ļ̳�ϵͳ�Դ���ʽ
				dialog=new Dialog(this,R.style.MyDialog);
				View view=View.inflate(this, R.layout.normal_entry_dialog, null);
				et_pwd =(EditText) view.findViewById(R.id.et_normal_entry_pwd);
				bt_normal_ok =(Button) view.findViewById(R.id.bt_normal_dialog_ok);
				bt_normal_cancel=(Button) view.findViewById(R.id.bt_normal_dialog_cancel);
				
				Log.i(TAG,"�����½���ڴ���");
				bt_normal_ok.setOnClickListener(this); //������ǰ���ڵİ�ť�ĵ���¼�
				bt_normal_cancel.setOnClickListener(this);
				dialog.setContentView(view);
				dialog.show();		
	}

	/**
	 * �����һ�ν���ʱ�ĶԻ���
	 */
	private void showFirstEntryDialog() {

		//����ϵͳĬ�϶Ի����tittle��background,���½���style�н����ļ̳�ϵͳ�Դ���ʽ
		dialog=new Dialog(this,R.style.MyDialog);
		
		//��ʾʲô�����أ���Ҫ�Ѳ��ִ�������
//		dialog.setContentView(R.layout.first_entry_dialog);//���ַ������ܴ����������İ�ť�ȿռ䣬�Ӷ��������°취
		
		//�취Ϊ���Ȱ�xmlת����view����
		View view=View.inflate(this, R.layout.first_entry_dialog, null);
		et_pwd =	(EditText) view.findViewById(R.id.et_first_entry_pwd);
		et_confirm=(EditText) view.findViewById(R.id.et_first_entry_pwd_confirm);
		bt_ok = (Button) view.findViewById(R.id.bt_first_dialog_ok);
		bt_cancel= (Button) view.findViewById(R.id.bt_first_dialog_cancel);
		
		Log.i(TAG,"����������");
		
		bt_ok.setOnClickListener(this);//������ǰ���ڵİ�ť�ĵ���¼�
		bt_cancel.setOnClickListener(this);
		dialog.setContentView(view);
		//����dialog��Ϳ���show������
		dialog.show();
		
	}

	/**
	 * ���sharepreferences�Ƿ�����������
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
		case R.id.bt_first_dialog_cancel:  //�����ȡ����ť
			
			dialog.dismiss();
			
			//����������
			finish();
			Intent intent_gobacktoMainAcitivity1=new Intent(LostProtectedActivity.this,MainActivity.class);
			startActivity(intent_gobacktoMainAcitivity1);
			
			break;
			
		case R.id.bt_first_dialog_ok:  //�����ȷ����ť
			String pwd=et_pwd.getText().toString().trim();
			String pwd_confirm=et_confirm.getText().toString().trim();
			
			if("".equals(pwd)||"".equals(pwd_confirm)){
				Toast.makeText(getApplicationContext(), "���벻��Ϊ��~", 0).show();
				return;
			}else {
				if(pwd.equals(pwd_confirm)){  //������Ⱦ��õ�������
					Editor editor= sp.edit();
					
					//������ת�������ı���
					String md5_pwd=MD5Encoder.enCode(pwd);
					editor.putString("password",md5_pwd );	
					//һ��Ҫ�ύ
					editor.commit();
					
					
					//�ύ�����ý���
					finish();
					Intent intent=new Intent(LostProtectedActivity.this,SetupGuideActivity.class);
					startActivity(intent);
					
				}else {
					Toast.makeText(getApplicationContext(), "�������벻ͬ", 0).show();
					return;
				}
			}
			//�Լ������Ĵ���һ��Ҫdismiss
			dialog.dismiss();
			break;
		case R.id.bt_normal_dialog_ok:  //�����ȷ����ť
			String normal_pwd=et_pwd.getText().toString().trim();
			if("".equals(normal_pwd)){
				Toast.makeText(getApplicationContext(), "����������", 0).show();
				return;
			}else {
				String  realPWD=sp.getString("password", "");
				if(realPWD.equals(MD5Encoder.enCode(normal_pwd))){
					if(isSetUp()){
						//����������
						Log.i(TAG, "�����ֻ�����������");
						
						//����ҳ���������ʽ��һ����
						setContentView(R.layout.lost_protected);
						
						//������contentview����Ѱ�����еĿؼ���
						 tv_lost_protected_number=(TextView) findViewById(R.id.tv_lost_protected_number);
						 tv_reentry_setup_guide=(TextView) findViewById(R.id.tv_reentry_setup_guide);
						 cb_isprotecting=(CheckBox) findViewById(R.id.cb_isprotecting);		
						 
						String number=sp.getString("safenumber", "");
						tv_lost_protected_number.setText("�ֻ���ȫ����Ϊ :"+number);
						
						//ȷ��һ������¼�,���½����򵼽���
						tv_reentry_setup_guide.setOnClickListener(this);
						
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
						
						
					}else {
						
						Log.i(TAG, "���������򵼽���");
						
						//�ѵ�ǰactivity finish��
						finish();
						Intent intent=new Intent(LostProtectedActivity.this,SetupGuideActivity.class);
						startActivity(intent);
					}
					
				}else {
					Toast.makeText(getApplicationContext(), "�������벻��ȷ", 0).show();
					return ;
				}
			}
			dialog.dismiss();
			break;
			
		case R.id.bt_normal_dialog_cancel:  //�����ȡ����ť
			dialog.dismiss();
			
			//����������
			finish();
			Intent intent_gobacktoMainAcitivity2=new Intent(LostProtectedActivity.this,MainActivity.class);
			startActivity(intent_gobacktoMainAcitivity2);
			
			break;
			
		case R.id.tv_reentry_setup_guide:  //����ǽ����򵼰�ť
			finish();
			Intent intent=new Intent(LostProtectedActivity.this,SetupGuideActivity.class);
			startActivity(intent);
		}
	}
	
	/**
	 * �ж��û��Ƿ��Ѿ������û���
	 * @return
	 */
	private boolean isSetUp(){
		boolean issetup=sp.getBoolean("isSetupReady", false);//���û��ֵ�ͷ���false���ֵ
		return issetup;
	} 
}
