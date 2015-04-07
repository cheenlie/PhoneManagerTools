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

		dao = new BlackNumberDAO(this);// ���봫��������

		// �������ڲ���ķ�ʽʵ��bt_call_sms_add�ĵ����
		bt_call_sms_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(CallSmsActivity.this);
				builder.setTitle("��Ӻ���������");
				final EditText et = new EditText(CallSmsActivity.this);

				// �����������ݵ�����
				et.setInputType(InputType.TYPE_CLASS_NUMBER);

				// bulider��ͨ��setview��������view��,���et����view����
				builder.setView(et); // û��new�ͻ���ʾ���������û�ж���
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Cannot refer to a non-final variable et
								// inside an inner class defined in a different
								// method
								// �������ڲ����������÷�final��Ա
								String number = et.getText().toString().trim();
								// �ж��ַ����Ƿ�Ϊ����TextUtils
								if (TextUtils.isEmpty(number)) {
									Toast.makeText(getApplicationContext(),
											"������벻��Ϊ��", 1).show();
									return;
								} else {
									dao.add(number);
									// todo,֪ͨlistview��������

//									// ��һ��������ȱ�㣺��ˢ������listview�����Ƽ�ʹ�á�
									numbers = dao.findAllNumbers();
//									// ����CallSmsActivity��thisǰ�ᱨ���´���˵������ʶ��this���������ڲ�������
//									// The constructor ArrayAdapter<String>(new
//									// DialogInterface.OnClickListener(){}, int,
//									// int, List<String>) is undefined
//									lv_call_sms
//											.setAdapter(new ArrayAdapter<String>(
//													CallSmsActivity.this,
//													R.layout.blacknumbers_item,
//													R.id.tv_blacknumbers_item,
//													numbers));
									// �ڶ�������,֪ͨ������������������
									adapter.notifyDataSetChanged();

								}

							}
						});
				builder.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// ������ȡ������ִ������շ���
							}
						});

				// ���ú�builder��Ҫshow����
				builder.create().show();

			}
		});

		numbers = dao.findAllNumbers();
		// ��listview������չ�ֳ����ͱ�����adapter
		adapter = new ArrayAdapter<String>(this, R.layout.blacknumbers_item,R.id.tv_blacknumbers_item, numbers);
		lv_call_sms.setAdapter(adapter);
	}
}
