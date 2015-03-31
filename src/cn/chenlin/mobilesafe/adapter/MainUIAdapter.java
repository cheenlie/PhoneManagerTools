package cn.chenlin.mobilesafe.adapter;

import cn.chenlin.mobilesafe.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainUIAdapter extends BaseAdapter {
	private  Context context;//����������
	private LayoutInflater inflater ;//���������ľͿ��Եõ�һ�������������
	private ImageView iv_icon;
	private TextView tv_name;
	
	//�ж�SharedPreferences���Ƿ���lost_name����,�ȵö���һ��SharedPreferences
	private SharedPreferences sp;//������Ҫ��ʼ�����ŵ����캯���г�ʼ��
	
	public MainUIAdapter(Context context) {
		this.context = context;
		inflater=LayoutInflater.from(context);//�������ķŽ���
		sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);//��ʼ�������Ҫ�жϣ��ж���getview��ʵ��
	}

	private static String[] names = {"�ֻ�����","ͨѶ��ʿ","�������",
							         "�������","��������","�ֻ�ɱ��",
							         "ϵͳ�Ż�","�߼�����","��������" };
	private static int[]  icons={
						R.drawable.widget05,R.drawable.widget02,R.drawable.widget01,
						R.drawable.widget07,R.drawable.widget05,R.drawable.widget04,
						R.drawable.widget06,R.drawable.widget03,R.drawable.widget08	};
	@Override
	public int getCount() {
		
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//����inflater�Ϳ����õ���Դ�ļ�������Դ�ļ�ת���������
		View view=inflater.inflate(R.layout.mainscreen_item,null);
		iv_icon=(ImageView) view.findViewById(R.id.iv_main_icon);
		tv_name= (TextView) view.findViewById(R.id.tv_main_name);
		
		//�ҵ�����󣬾Ϳ��Ը�����ֵ��
		iv_icon.setImageResource(icons[position]);
		tv_name.setText(names[position]);
		
		//�ж�����ǵ�0����Ŀ�ǾͿ���SharedPreferences�е��Ƿ��и�����Ϣ
		if(position==0){
			 String name= sp.getString("lost_name", null);//�õ���0��Ŀ��name�����û�����ݾ���Ϊ��
			 if(name!=null){
				 tv_name.setText(name);
			 } 
		}
		
		//����ֵ��ϾͿ��Է��ض�����
		return view;
	}

}
