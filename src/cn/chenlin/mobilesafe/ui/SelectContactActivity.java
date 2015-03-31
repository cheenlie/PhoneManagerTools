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
		//先把布局列出来
		 setContentView(R.layout.select_contact);
		
		 ContactInfoService service= new ContactInfoService(this);    //
		 infos=service.getContactInfos();                             //这里要先获取信息
		                                                              //再初始化lv控件
		 lv=(ListView) this.findViewById(R.id.lv_select_contact);     //要不会在“return infos.size();”
			//创建数据适配器把数据显示出来                                                                                                               //出现空指针异常
		 lv.setAdapter(new SelectContactAdapter());                   //
		 
		 //激活这个待返回值的页面后，就可以操作数据啦
		 lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				String phone=infos.get(position).getPhone();
				Intent intent=new Intent();
				//把phone值放到
				intent.putExtra("number", phone);
				//返回电话号码，但是必须返回intent集
				setResult(0,intent);
				finish();//关闭当前页面
			}
		});
		
	}
	//为了简化代码，就建立一个内部类，来实现adapter
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
			//创建一个显示控件(数量不多，数量多就要创建一个页面来负责显示)
			LinearLayout ll=new LinearLayout(SelectContactActivity.this);
			//设置一下ll的方向
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView tv_name=new TextView(SelectContactActivity.this);
			tv_name.setText("姓名"+info.getName());
			TextView tv_phone=new TextView(SelectContactActivity.this);
			tv_phone.setText("电话号码"+info.getPhone());
				
			ll.addView(tv_name);
			ll.addView(tv_phone);
			
			
			return ll;
		}
		
	}

}
