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
		//把界面写好后，布置到逻辑中来
		setContentView(R.layout.setupguide3);
		
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		
		bt_next=(Button)this.findViewById(R.id.bt_next);//在不同的activity里，文件名是可以重复的
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
			//判断是否下一步为空
			if("".equals(number)){
				Toast.makeText(this, "安全号码不能为空", 0).show();
				return ;
			}else {
				//得到内容编辑器
				Editor editor=sp.edit();
				editor.putString("safenumber", number);
				editor.commit();
			
			}
			
			Intent intent4= new Intent(this,SetupGuide4Activity.class);
			finish();
			startActivity(intent4);
			
			overridePendingTransition(R.anim.translate_in , R.anim.translate_out);
			//进入效果中，100%p表示右边边框，-100%p表示界面100出去
			break;
		
		case R.id.bt_select_contact:
			//intent参数：显示的上下文;激活的组件
			Intent intent=new Intent(this,SelectContactActivity.class);
			
			//激活一个带返回值的页面，注意和“startActivity(intent);”的区别
			startActivityForResult(intent, 0);//请求码给个0便可以了
			//上面那句便打开了selectcontactactivity这个页面
			
			//当激活的界面结束后，我们需要重写onActivityResult方法，如下
			
			break;
		}
	}
	/**
	 * 重写的onactivityresult方法
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		//拿我们需要的数据啦
		if(data!=null){
			String number=data.getStringExtra("number");
			
			//拿到号码后我们就可以把号码设置到界面上了
			et_number.setText(number);
		}
	}
	

}
