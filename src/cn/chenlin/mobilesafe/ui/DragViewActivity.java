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
		iv_dragview = (ImageView) this.findViewById(R.id.iv_drag_view);
		tv_dragview_lable = (TextView) this.findViewById(R.id.tv_drag_view_lable);	
		sp=getSharedPreferences("config", MODE_PRIVATE);
		//�����ϴ�imageview��λ��
		int x=sp.getInt("lastx", 0);  //Ĭ����0
		int y=sp.getInt("lasty", 0);
		iv_dragview.layout(iv_dragview.getLeft()+x, iv_dragview.getTop()+y, iv_dragview.getRight()+x, iv_dragview.getBottom()+y);
		
		//������ʱ�䶨��һ��������
		iv_dragview.setOnTouchListener(this);
	
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (v.getId()) {
		//�������drag_view�ؼ���
		case R.id.iv_drag_view:
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:  //��ָ��һ�η���ȥ���¼�
				startX=(int) event.getRawX();
				startY=(int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:  //�ƶ��¼�
				//��ȡ��ָ�ƶ�ʱ������
				int x=(int) event.getRawX();
				int y=(int) event.getRawY();
				
				if(y<=240){
					tv_dragview_lable.layout(tv_dragview_lable.getLeft(), 220, tv_dragview_lable.getRight(),280);
				}else {
					tv_dragview_lable.layout(tv_dragview_lable.getLeft(), 20, tv_dragview_lable.getRight(),80);
				}
				
				//�ƶ��ľ���
				int dx=x-startX;
				int dy=y-startY;
				
				int l=iv_dragview.getLeft();
				int t=iv_dragview.getTop();
				int r=iv_dragview.getRight();
				int b=iv_dragview.getBottom();
				
				//�ı�layout��λ��
				//��Ϊ���ĸ���ȷ��layout��λ�ã����Բ���ֱ�Ӱ���ָ���ڵĵ��λ��ֱ�ӵ��뵽view�ؼ���
				iv_dragview.layout(l+dx, t+dy, r+dx, b+dy);
				
				//����ָ���һ�ε�λ�ñ�������
				startX=(int) event.getRawX();
				startY=(int) event.getRawY();
				
				break;
			case MotionEvent.ACTION_UP:   //��ָ�뿪��Ļ
				Log.i(TAG,"��ָ�뿪��Ļ" );
				
				//���뿪��Ļ��ʱ���¼��iv_dragview��λ��
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
		return true;  //Ĭ������£�return false�������¼���ֻ��һ�Σ����Ǹĳ�true���¼���һֱ��ͣ�Ĳ�����
	}
}
