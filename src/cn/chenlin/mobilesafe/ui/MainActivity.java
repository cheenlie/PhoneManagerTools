package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.adapter.MainUIAdapter;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener   {
	
	private static final String TAG = "MainActivity";
	private GridView gv_main;
	
	//用于持久化数据（配置信息）
	private SharedPreferences sp;
	
	//新建一个我们自己建立的适配器
	private MainUIAdapter adapter;
	
	//为了方便测试重写oncreate方法
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		
		//初始化，会产生config的 xml文件，采用私有话的访问模式
		sp=this.getSharedPreferences("config",MODE_PRIVATE);
		
		gv_main=(GridView) findViewById(R.id.gv_main);
		adapter=new MainUIAdapter(this);
		
		//适配好后就把对象放到gv_main中
		gv_main.setAdapter(adapter);
		gv_main.setOnItemClickListener(this);
		
		//长按某个条目更改的名字
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					int position, long id) {
				
				//只处理长按第一个按钮，其他的没处理
				if(position==0){
					AlertDialog.Builder builder=new Builder(MainActivity.this);
					builder.setTitle("设置");
					builder.setMessage("请输入要更改的条目名");
					
					final	EditText et=new EditText(MainActivity.this);
					et.setHint("请输入文本");
					builder.setView(et);
					
					builder.setPositiveButton("确认", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//因为是内部类访问外部类的方法，所以把edittext做出final型的
							String  name=et.getText().toString().trim();//去掉前后两端的空格						
							if("".equals(name)){
								Toast.makeText(getApplicationContext(),"输入名字不能为空", 1);
								return;
							}else {
								//取出来数据，然后把数据持久化，持久化使用sharepreferences
								
								//创建一个editor对象来放名字
								Editor editor=sp.edit();
								//创建业务逻辑名字lost_name，它对应的值就这个name复制进来的值 
								editor.putString("lost_name", name);
								editor.commit();//完成数据提交
								
								//数据提交后，更改当前view的文本
								TextView tv= (TextView) view.findViewById(R.id.tv_main_name);
								//拿到textview内容后，我们就可以把内容设置进来
								tv.setText(name);
								
								//下次用户登陆的时候，就需要判断名字是否更改过，因此就要在mainactivity里面处理判断，判断SharedPreferences
								//里是否有一个lost_name的参数，如果有，那就取出来显示出来。
								
							}
						}
					});
					
					builder.setNegativeButton("取消", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					
					//按键处理完后要把对话框显示出来				
					builder.create().show();
				}
				return false;
			}
		});
	}

	/**
	 * 当gridview的条目被点击的时候对应的回调
	 * parent: 代表父容器gridview
	 * view:  当前点击的条目 linearlayout
	 * position： 点击条目对应的位置 
	 * id： 代表行号，但一般用不上 
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i(TAG, "点击的位置"+position);
		switch (position) {
		case 0:
			Log.i(TAG, "进入手机防盗");
			Intent  lostiIntent=new Intent(MainActivity.this,LostProtectedActivity.class);
			startActivity(lostiIntent);
			break;
		case 5:
			Log.i(TAG, "进入手机杀毒");
			Intent  AntivirusIntent=new Intent(MainActivity.this,AntivirusActivity.class);
			startActivity(AntivirusIntent);
			break;
		}
	}
}
