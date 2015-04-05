package cn.chenlin.mobilesafe.ui;

import java.io.File;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.*;
import cn.chenlin.mobilesafe.service.AddressService;
import android.R.string;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AdToolsActivity extends Activity implements OnClickListener {

	protected static final int ERROR = 10;
	protected static final int SUCCESS = 9;
	private TextView tv_adtools_query;
	private ProgressDialog pd;
	private TextView tv_adtools_service_status;
	private CheckBox cb_adtools_status_control;
	private Intent serviceIntent;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case ERROR:
				Toast.makeText(getApplicationContext(), "���ش���", 0).show();
				break;
			case SUCCESS:
				Toast.makeText(getApplicationContext(), "�������", 0).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.adtools);
		tv_adtools_service_status = (TextView) this
				.findViewById(R.id.tv_adtools_service_status);
		cb_adtools_status_control = (CheckBox) this
				.findViewById(R.id.cb_adtools_status_control);

		serviceIntent = new Intent(this, AddressService.class);

		cb_adtools_status_control
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton ButtonView,
							boolean isChecked) {
						if(isChecked){
							startService(serviceIntent);
							tv_adtools_service_status.setTextColor(Color.WHITE);
							tv_adtools_service_status.setText("�����ط����Ѿ�����");
						}else {
							stopService(serviceIntent);
							tv_adtools_service_status.setTextColor(Color.RED);
							tv_adtools_service_status.setText("�����ط���δ����");
						}
						
					}
				});

		tv_adtools_query = (TextView) this.findViewById(R.id.tv_adtools_query);
		tv_adtools_query.setOnClickListener(this);
	}

	@Override
	public void onClick(View viewid) {
		switch (viewid.getId()) {
		case R.id.tv_adtools_query:
			if (isDBexist()) {
				Intent adtools_query = new Intent(AdToolsActivity.this,
						QueryNumberActivity.class);
				startActivity(adtools_query);
			} else {
				// ��ʾ���ضԻ���
				pd = new ProgressDialog(this);
				pd.setMessage("�����������ݿ��ļ�");

				// ���ý�����Ϊˮƽ״��
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();

				// ��ʼ�������ݿ⣬Ҫ����һ�����߳�
				new Thread() {
					@Override
					public void run() {
						// getResources()��ȡconfig����Ĳ���
						String path = getResources().getString(
								R.string.addressurl);
						String filepath = "/sdcard/address.db";
						Message msg = new Message();
						try {

							File file = DownLoadFileTask.getFile(path,
									filepath, pd);
							pd.dismiss();

							msg.what = SUCCESS;
							handler.sendMessage(msg);

						} catch (Exception e) {
							e.printStackTrace();
							pd.dismiss();

							msg.what = ERROR;
							handler.sendMessage(msg);
						}
					}
				}.start();
			}
			break;
		}
	}

	/*
	 * �ж����ݿ��Ƿ���ڣ�������ھͷ���Ϊtrue
	 */
	public boolean isDBexist() {
		// db=SQLiteDatabase.openDatabase("/sdcard/address.db", null,
		// SQLiteDatabase.OPEN_READONLY);
		// if(db.equals(null))
		// return false;
		// else {
		// return true;
		// }
		File file = new File("/sdcard/address.db");
		return file.exists();
	}
}
