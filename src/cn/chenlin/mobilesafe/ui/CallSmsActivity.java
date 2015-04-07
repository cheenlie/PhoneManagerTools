package cn.chenlin.mobilesafe.ui;

import java.security.PublicKey;
import java.util.List;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.db.dao.BlackNumberDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CallSmsActivity extends Activity {

	private ListView lv_call_sms;
	private Button bt_call_sms_add;
	private BlackNumberDAO dao;
	private List<String> numbers;
	private ArrayAdapter<String> adapter; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.call_sms_safe);
		lv_call_sms = (ListView) this.findViewById(R.id.lv_call_sms_safe);
		bt_call_sms_add = (Button) this.findViewById(R.id.bt_call_sms_safe);

		dao = new BlackNumberDAO(this);// 必须传进上下文

		// 用匿名内部类的方式实现bt_call_sms_add的点击类
		bt_call_sms_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(CallSmsActivity.this);
				builder.setTitle("添加黑名单号码");
				final EditText et = new EditText(CallSmsActivity.this);

				// 设置输入数据的类型
				et.setInputType(InputType.TYPE_CLASS_NUMBER);

				// bulider是通过setview往里面设view的,这个et便是view对象
				builder.setView(et); // 没有new就会提示你这个方法没有定义
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Cannot refer to a non-final variable et
								// inside an inner class defined in a different
								// method
								// 不能在内部类里面引用非final成员
								String number = et.getText().toString().trim();
								// 判断字符串是否为空用TextUtils
								if (TextUtils.isEmpty(number)) {
									Toast.makeText(getApplicationContext(),
											"输入号码不能为空", 1).show();
									return;
								} else {
									dao.add(number);
									// todo,通知listview更新数据

//									// 第一种做法，缺点：会刷新整个listview，不推荐使用。
									numbers = dao.findAllNumbers();
//									// 不加CallSmsActivity在this前会报如下错误，说明不能识别this，估计由内部类引起
//									// The constructor ArrayAdapter<String>(new
//									// DialogInterface.OnClickListener(){}, int,
//									// int, List<String>) is undefined
//									lv_call_sms
//											.setAdapter(new ArrayAdapter<String>(
//													CallSmsActivity.this,
//													R.layout.blacknumbers_item,
//													R.id.tv_blacknumbers_item,
//													numbers));
									// 第二种做法,通知数据适配器更新数据
									adapter.notifyDataSetChanged();

								}

							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 如果点击取消，就执行这个空方法
							}
						});

				// 设置好builder后要show出来
				builder.create().show();

			}
		});

		numbers = dao.findAllNumbers();
		// 给listview把数据展现出来就必须用adapter
		adapter = new ArrayAdapter<String>(this, R.layout.blacknumbers_item,R.id.tv_blacknumbers_item, numbers);
		lv_call_sms.setAdapter(adapter);
	}
}
