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
	private  Context context;//创建上下文
	private LayoutInflater inflater ;//有了上下文就可以得到一个布局填充器了
	private ImageView iv_icon;
	private TextView tv_name;
	
	//判断SharedPreferences里是否有lost_name参数,先得定义一个SharedPreferences
	private SharedPreferences sp;//创建后要初始化，放到构造函数中初始化
	
	public MainUIAdapter(Context context) {
		this.context = context;
		inflater=LayoutInflater.from(context);//把上下文放进来
		sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);//初始化后就需要判断，判断在getview中实现
	}

	private static String[] names = {"手机防盗","通讯卫士","软件管理",
							         "任务管理","上网管理","手机杀毒",
							         "系统优化","高级工具","设置中心" };
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
		//有了inflater就可以拿到资源文件，把资源文件转化成类对象
		View view=inflater.inflate(R.layout.mainscreen_item,null);
		iv_icon=(ImageView) view.findViewById(R.id.iv_main_icon);
		tv_name= (TextView) view.findViewById(R.id.tv_main_name);
		
		//找到对象后，就可以给对象赋值了
		iv_icon.setImageResource(icons[position]);
		tv_name.setText(names[position]);
		
		//判断如果是第0个条目那就看看SharedPreferences中的是否有更改信息
		if(position==0){
			 String name= sp.getString("lost_name", null);//得到第0条目的name，如果没有内容就置为空
			 if(name!=null){
				 tv_name.setText(name);
			 } 
		}
		
		//对象赋值完毕就可以返回对象了
		return view;
	}

}
