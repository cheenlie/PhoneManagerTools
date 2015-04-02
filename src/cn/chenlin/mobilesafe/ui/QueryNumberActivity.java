package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryNumberActivity extends Activity {

	private EditText et_number;
	private TextView tv_queryres;
	private Animation shake;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.query);
		tv_queryres=(TextView) this.findViewById(R.id.tv_query_number_res);
		et_number=(EditText) this.findViewById(R.id.et_num);
	}

	//在query.xml文件中定义点击事件
	public void query(View v){
		String num=et_number.getText().toString().trim();
		if(TextUtils.isEmpty(num))
		{//为空的时候窗口抖动
			shake=AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
		}else{
				//打开数据库
		}
	}
	
}
