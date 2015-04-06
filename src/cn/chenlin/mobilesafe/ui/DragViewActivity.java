package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DragViewActivity extends Activity implements OnTouchListener {

	private static final String TAG = "DragViewActivity";
	private ImageView iv_dragview;
	private TextView tv_dragview_lable;
	int startX;
	int startY;
	private SharedPreferences sp;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dragview);
		iv_dragview = (ImageView) this.findViewById(R.id.iv_drag_view);//①
		tv_dragview_lable = (TextView) this.findViewById(R.id.tv_drag_view_lable);	
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//加载上次imageview的位置[存在问题：这下面三句话执行完毕可能上面的①还没有执行完毕，所以最后画出的是初始位置]
		//把这三句话放到getresume生命周期中，就能保证先执行①再执行下面三句话
//		int x=sp.getInt("lastx", 0);  //默认是0
//		int y=sp.getInt("lasty", 0);
//		iv_dragview.layout(iv_dragview.getLeft()+x, iv_dragview.getTop()+y, iv_dragview.getRight()+x, iv_dragview.getBottom()+y);
		
		//给触摸时间定义一个监听器
		iv_dragview.setOnTouchListener(this);
	}
	
	

	//onresume是在当控件都显示出来，并且获得焦点后才会调用
	@Override
	protected void onResume() {   
		super.onResume();
		int x=sp.getInt("lastx", 0);  //默认是0
		int y=sp.getInt("lasty", 0);
		//重新渲染view控件：方法一，此处无效
//		iv_dragview.layout(iv_dragview.getLeft()+x, iv_dragview.getTop()+y, iv_dragview.getRight()+x, iv_dragview.getBottom()+y);
		//iv_dragview.invalidate();//让这个控件重新去渲染一下view控件
		//重新渲染view控件：方法二，肯定有效
		//注意导入的默认包不是“android.view.WindowManager.LayoutParams;”是RelativeLayout才可以
		LayoutParams params=(LayoutParams) iv_dragview.getLayoutParams(); //获取iv_dragview的布局
		params.leftMargin=x;//离左边窗体的距离
		params.topMargin=y;
		iv_dragview.setLayoutParams(params);
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (v.getId()) {
		//如果放在drag_view控件上
		case R.id.iv_drag_view:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:  //手指第一次放上去的事件
				startX=(int) event.getRawX();
				startY=(int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:  //移动事件
				//获取手指移动时的坐标
				int x=(int) event.getRawX();
				int y=(int) event.getRawY();
				
				if(y<=240){
					tv_dragview_lable.layout(tv_dragview_lable.getLeft(), 220, tv_dragview_lable.getRight(),280);
				}else {
					tv_dragview_lable.layout(tv_dragview_lable.getLeft(), 20, tv_dragview_lable.getRight(),80);
				}
				
				//移动的距离
				int dx=x-startX;
				int dy=y-startY;
				
				int l=iv_dragview.getLeft();
				int t=iv_dragview.getTop();
				int r=iv_dragview.getRight();
				int b=iv_dragview.getBottom();
				
				//改变layout的位置
				//因为是四个点确定layout的位置，所以不能直接把手指所在的点的位置直接导入到view控件中
				iv_dragview.layout(l+dx, t+dy, r+dx, b+dy);
				
				//把手指最后一次的位置保存下来
				startX=(int) event.getRawX();
				startY=(int) event.getRawY();
				
				break;
			case MotionEvent.ACTION_UP:   //手指离开屏幕
				Log.i(TAG,"手指离开屏幕" );
				
				//手离开屏幕的时候记录下iv_dragview的位置
				int lastx=iv_dragview.getLeft();
				int lasty=iv_dragview.getTop();
				Editor editor=sp.edit();
				editor.putInt("lastx", lastx);
				editor.putInt("lasty", lasty);
				editor.commit();
				
				break;
			}
			break;
		}
		return true;  //默认情况下（return false）触摸事件在只有一次，但是改成true后事件会一直不停的产生。
	}
}
