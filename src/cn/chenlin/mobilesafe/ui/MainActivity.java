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
	
	//���ڳ־û����ݣ�������Ϣ��
	private SharedPreferences sp;
	
	//�½�һ�������Լ�������������
	private MainUIAdapter adapter;
	
	//Ϊ�˷��������дoncreate����
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen);
		
		//��ʼ���������config�� xml�ļ�������˽�л��ķ���ģʽ
		sp=this.getSharedPreferences("config",MODE_PRIVATE);
		
		gv_main=(GridView) findViewById(R.id.gv_main);
		adapter=new MainUIAdapter(this);
		
		//����ú�ͰѶ���ŵ�gv_main��
		gv_main.setAdapter(adapter);
		gv_main.setOnItemClickListener(this);
		
		//����ĳ����Ŀ���ĵ�����
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					int position, long id) {
				
				//ֻ��������һ����ť��������û����
				if(position==0){
					AlertDialog.Builder builder=new Builder(MainActivity.this);
					builder.setTitle("����");
					builder.setMessage("������Ҫ���ĵ���Ŀ��");
					
					final	EditText et=new EditText(MainActivity.this);
					et.setHint("�������ı�");
					builder.setView(et);
					
					builder.setPositiveButton("ȷ��", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//��Ϊ���ڲ�������ⲿ��ķ��������԰�edittext����final�͵�
							String  name=et.getText().toString().trim();//ȥ��ǰ�����˵Ŀո�						
							if("".equals(name)){
								Toast.makeText(getApplicationContext(),"�������ֲ���Ϊ��", 1);
								return;
							}else {
								//ȡ�������ݣ�Ȼ������ݳ־û����־û�ʹ��sharepreferences
								
								//����һ��editor������������
								Editor editor=sp.edit();
								//����ҵ���߼�����lost_name������Ӧ��ֵ�����name���ƽ�����ֵ 
								editor.putString("lost_name", name);
								editor.commit();//��������ύ
								
								//�����ύ�󣬸��ĵ�ǰview���ı�
								TextView tv= (TextView) view.findViewById(R.id.tv_main_name);
								//�õ�textview���ݺ����ǾͿ��԰��������ý���
								tv.setText(name);
								
								//�´��û���½��ʱ�򣬾���Ҫ�ж������Ƿ���Ĺ�����˾�Ҫ��mainactivity���洦���жϣ��ж�SharedPreferences
								//���Ƿ���һ��lost_name�Ĳ���������У��Ǿ�ȡ������ʾ������
								
							}
						}
					});
					
					builder.setNegativeButton("ȡ��", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					
					//�����������Ҫ�ѶԻ�����ʾ����				
					builder.create().show();
				}
				return false;
			}
		});
	}

	/**
	 * ��gridview����Ŀ�������ʱ���Ӧ�Ļص�
	 * parent: ��������gridview
	 * view:  ��ǰ�������Ŀ linearlayout
	 * position�� �����Ŀ��Ӧ��λ�� 
	 * id�� �����кţ���һ���ò��� 
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i(TAG, "�����λ��"+position);
		switch (position) {
		case 0:
			Log.i(TAG, "�����ֻ�����");
			Intent  lostiIntent=new Intent(MainActivity.this,LostProtectedActivity.class);
			startActivity(lostiIntent);
			break;
		case 5:
			Log.i(TAG, "�����ֻ�ɱ��");
			Intent  AntivirusIntent=new Intent(MainActivity.this,AntivirusActivity.class);
			startActivity(AntivirusIntent);
			break;
		}
	}
}
