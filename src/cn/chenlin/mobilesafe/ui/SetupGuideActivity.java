package cn.chenlin.mobilesafe.ui;


import cn.chenlin.mobilesafe.R;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetupGuideActivity extends Activity implements OnClickListener {
	private static final String TAG = "SetupGuideActivity";
	private Button bt_next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//�ѽ���д�ú󣬲��õ��߼�����
		setContentView(R.layout.setupguide1);
		bt_next=(Button) findViewById(R.id.bt_next);
		bt_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_next:
			Log.i(TAG,"����guide2��");
			//������
			Intent intent= new Intent(SetupGuideActivity.this,SetupGuide2Activity.class);
			//�ѵ�ǰ����ջ�е�activity�Ƴ�
			finish();
			startActivity(intent);
			
			//����activity�л�ʱ�Ķ���Ч��
			//0.0f��ȫ͸����1.0f�ǲ�͸��;alpha���Ƕ���Ч��
			overridePendingTransition(R.anim.alpha_in , R.anim.alpha_out);
			
			break;

		}
	}

}
