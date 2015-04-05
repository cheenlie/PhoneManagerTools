package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.NumberAddressService;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryNumberActivity extends Activity {

	private EditText et_number;
	private TextView tv_query_res;
	private Animation shake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.query);
		tv_query_res=(TextView) this.findViewById(R.id.tv_query_number_res);
		et_number=(EditText) this.findViewById(R.id.et_num);
	}

	//��query.xml�ļ��ж������¼�
	public void query(View v){
		String num=et_number.getText().toString().trim();
		if(TextUtils.isEmpty(num))
		{//Ϊ�յ�ʱ�򴰿ڶ���
			shake=AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
		}else{
				//�����ݿ�
			String numString=NumberAddressService.getNumberAddress(num);
			tv_query_res.setText("�����أ�"+numString);
		}
	}
	
}