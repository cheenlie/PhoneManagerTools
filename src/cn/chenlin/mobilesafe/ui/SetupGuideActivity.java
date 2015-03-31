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
		//把界面写好后，布置到逻辑中来
		setContentView(R.layout.setupguide1);
		bt_next=(Button) findViewById(R.id.bt_next);
		bt_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_next:
			Log.i(TAG,"进入guide2了");
			//上下文
			Intent intent= new Intent(SetupGuideActivity.this,SetupGuide2Activity.class);
			//把当前任务栈中的activity移除
			finish();
			startActivity(intent);
			
			//设置activity切换时的动画效果
			//0.0f是全透明，1.0f是不透明;alpha就是动画效果
			overridePendingTransition(R.anim.alpha_in , R.anim.alpha_out);
			
			break;

		}
	}

}
