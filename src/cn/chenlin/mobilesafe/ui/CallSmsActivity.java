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
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsActivity extends Activity {

	private static final String TAG = "CallSmsActivity";
	private ListView lv_call_sms;
	private Button bt_call_sms_add;
	private BlackNumberDAO dao;
	private List<String> numbers;
	private CallSmsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.call_sms_safe);
		lv_call_sms = (ListView) this.findViewById(R.id.lv_call_sms_safe);
		bt_call_sms_add = (Button) this.findViewById(R.id.bt_call_sms_safe);

		dao = new BlackNumberDAO(this);// ���봫��������
		
		// ��listviewע�������Ĳ˵�
		registerForContextMenu(lv_call_sms);
		
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
//		adapter = new ArrayAdapter<String>(this, R.layout.blacknumbers_item,R.id.tv_blacknumbers_item, numbers);
		adapter=new CallSmsAdapter();
		lv_call_sms.setAdapter(adapter);
	}
	
	//��дһ��onstart��������������ڽ����ɿɼ������ʱ�����
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent=getIntent();//��ȡ����activity��������intent
		if(intent.getStringExtra("number")!=null){
			Log.i(TAG,"��ʾ�û���Ӻ�����");
		}
		
	}
	
	//��ע���listview�˵��������
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.context_menu, menu);
	}
	


	//��������Ŀ��ѡ�к󶼻�ִ�е�
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  //��ǰ��Ŀ��id
	  int id = (int) info.id;
	  //��ú���
	  String number = numbers.get(id);
	  switch (item.getItemId()) {
	  case R.id.update_number:
		  updateBlackNumber(number);
		  break;
	  case R.id.delete_number:
		  dao.delete(number);
		  // ���»�ȡ����������
		  numbers = dao.findAllNumbers();
		  //  ֪ͨlistview���½���
		  adapter.notifyDataSetChanged();
		  break;

	  }
	return false;
	}
	
	/**
	 * ���ĺ���������
	 * @param oldnumber
	 */
	
	private void updateBlackNumber(final String oldnumber) {
		AlertDialog.Builder builder = new Builder(CallSmsActivity.this);
		builder.setTitle("���ĺ���������");
		final EditText et = new EditText(CallSmsActivity.this);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(et); // û��new�ͻ���ʾ���������û�ж���
		builder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						String newnumber = et.getText().toString().trim();
						if (TextUtils.isEmpty(newnumber)) {
							Toast.makeText(getApplicationContext(),
									"������벻��Ϊ��", 1).show();
							return;
						} else {
							dao.update(oldnumber, newnumber);
							numbers = dao.findAllNumbers();
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


	//�Լ�����һ��adapter�����adapter����������numbers�ģ����Է���numbers���������
	private class CallSmsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return numbers.size();
		}

		@Override
		public Object getItem(int position) {
			return numbers.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view= View.inflate(CallSmsActivity.this, R.layout.blacknumbers_item, null);
			//�ҵ������������ֵ
			TextView textView= (TextView) view.findViewById(R.id.tv_blacknumbers_item);
			//��textview��ֵ,����positionλ�õ�ֵ
			textView.setText(numbers.get(position)); 
			return view;
		}}
}
