package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import android.app.Activity;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dragview);
		iv_dragview = (ImageView) this.findViewById(R.id.iv_drag_view);
		tv_dragview_lable = (TextView) this.findViewById(R.id.tv_drag_view_lable);	
		
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
				break;
				
				
			}
			break;
		}
		return true;  //Ĭ������£�return false�������¼���ֻ��һ�Σ����Ǹĳ�true���¼���һֱ��ͣ�Ĳ�����
	}
}
