package cn.chenlin.mobilesafe.ui;

import java.util.List;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.ContactInfoService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import cn.chenlin.mobilesafe.domain.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactActivity extends Activity {

	private ListView lv ;
	private List<ContactInfo> infos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 super.onCreate(savedInstanceState);
		//�ȰѲ����г���
		 setContentView(R.layout.select_contact);
		
		 ContactInfoService service= new ContactInfoService(this);    //
		 infos=service.getContactInfos();                             //����Ҫ�Ȼ�ȡ��Ϣ
		                                                              //�ٳ�ʼ��lv�ؼ�
		 lv=(ListView) this.findViewById(R.id.lv_select_contact);     //Ҫ�����ڡ�return infos.size();��
			//����������������������ʾ����                                                                                                               //���ֿ�ָ���쳣
		 lv.setAdapter(new SelectContactAdapter());                   //
		 
		 //�������������ֵ��ҳ��󣬾Ϳ��Բ���������
		 lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				String phone=infos.get(position).getPhone();
				Intent intent=new Intent();
				//��phoneֵ�ŵ�
				intent.putExtra("number", phone);
				//���ص绰���룬���Ǳ��뷵��intent��
				setResult(0,intent);
				finish();//�رյ�ǰҳ��
			}
		});
		
	}
	//Ϊ�˼򻯴��룬�ͽ���һ���ڲ��࣬��ʵ��adapter
	private class SelectContactAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View converView, ViewGroup parent) {
			ContactInfo info=infos.get(position);
			//����һ����ʾ�ؼ�(�������࣬�������Ҫ����һ��ҳ����������ʾ)
			LinearLayout ll=new LinearLayout(SelectContactActivity.this);
			//����һ��ll�ķ���
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView tv_name=new TextView(SelectContactActivity.this);
			tv_name.setText("����"+info.getName());
			TextView tv_phone=new TextView(SelectContactActivity.this);
			tv_phone.setText("�绰����"+info.getPhone());
				
			ll.addView(tv_name);
			ll.addView(tv_phone);
			
			
			return ll;
		}
		
	}

}
