package cn.chenlin.mobilesafe.ui;

import cn.chenlin.mobilesafe.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DragViewActivity extends Activity implements OnTouchListener {

	private ImageView iv_dargview;
	private TextView tv_dragview_lable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dragview);
		iv_dargview = (ImageView) this.findViewById(R.id.iv_drag_view);
		tv_dragview_lable = (TextView) this.findViewById(R.id.tv_drag_view_lable);	
		
		//������ʱ�䶨��һ��������
		iv_dargview.setOnTouchListener(this);
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		switch (v.getId()) {
		//�������drag_view�ؼ���
		case R.id.iv_drag_view:
			
			break;
		}
		return false;
	}
}
