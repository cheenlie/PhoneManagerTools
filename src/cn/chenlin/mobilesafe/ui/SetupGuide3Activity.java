package cn.chenlin.mobilesafe.ui;


import cn.chenlin.mobilesafe.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SetupGuide3Activity extends Activity implements OnClickListener {

	private Button bt_next;
	private Button bt_pre;
	private Button bt_select_contack;
	private SharedPreferences sp;
	private EditText et_number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//�ѽ���д�ú󣬲��õ��߼�����
		setContentView(R.layout.setupguide3);
		
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		
		bt_next=(Button)this.findViewById(R.id.bt_next);//�ڲ�ͬ��activity��ļ����ǿ����ظ���
		bt_pre=(Button) this.findViewById(R.id.bt_previous);
		bt_select_contack=(Button) this.findViewById(R.id.bt_select_contact);
		et_number=(EditText) this.findViewById(R.id.et_setup3_number);
		
		bt_next.setOnClickListener(this);
		bt_pre.setOnClickListener(this);
		bt_select_contack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_previous:
			Intent intent2= new Intent(this,SetupGuide2Activity.class);
			finish();
			startActivity(intent2);
			overridePendingTransition(R.anim.alpha_in , R.anim.alpha_out);
			
			break;
		case R.id.bt_next:
			String 	number=et_number.getText().toString().trim();
			//�ж��Ƿ���һ��Ϊ��
			if("".equals(number)){
				Toast.makeText(this, "��ȫ���벻��Ϊ��", 0).show();
				return ;
			}else {
				//�õ����ݱ༭��
				Editor editor=sp.edit();
				editor.putString("safenumber", number);
				editor.commit();
			
			}
			
			Intent intent4= new Intent(this,SetupGuide4Activity.class);
			finish();
			startActivity(intent4);
			
			overridePendingTransition(R.anim.translate_in , R.anim.translate_out);
			//����Ч���У�100%p��ʾ�ұ߱߿�-100%p��ʾ����100��ȥ
			break;
		
		case R.id.bt_select_contact:
			//intent��������ʾ��������;��������
			Intent intent=new Intent(this,SelectContactActivity.class);
			
			//����һ��������ֵ��ҳ�棬ע��͡�startActivity(intent);��������
			startActivityForResult(intent, 0);//���������0�������
			//�����Ǿ�����selectcontactactivity���ҳ��
			
			//������Ľ��������������Ҫ��дonActivityResult����������
			
			break;
		}
	}
	/**
	 * ��д��onactivityresult����
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		//��������Ҫ��������
		if(data!=null){
			String number=data.getStringExtra("number");
			
			//�õ���������ǾͿ��԰Ѻ������õ���������
			et_number.setText(number);
		}
	}
	

}
