package cn.chenlin.mobilesafe.ui;

import java.io.File;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.engine.*;
import cn.chenlin.mobilesafe.service.AddressService;
import cn.chenlin.mobilesafe.service.BackupSmsService;
import cn.chenlin.mobilesafe.service.SmsInfoService;
import android.R.string;
import android.R.style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ShareCompat;
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
	private TextView tv_setbg;
	private SharedPreferences sp;
	private TextView tv_change_location;
	private TextView tv_sms_backup;
	private TextView tv_sms_restore;
	private  SmsInfoService smsInfoService;

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
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		tv_sms_backup=(TextView) findViewById(R.id.tv_adtools_sms_backup);
		tv_sms_restore=(TextView) findViewById(R.id.tv_adtools_sms_restore);
		tv_sms_backup.setOnClickListener(this);
		tv_sms_restore.setOnClickListener(this);
		
		tv_adtools_service_status = (TextView) this
				.findViewById(R.id.tv_adtools_service_status);
		cb_adtools_status_control = (CheckBox) this
				.findViewById(R.id.cb_adtools_status_control);
		tv_change_location=(TextView) this.findViewById(R.id.tv_adtools_change_location);
		tv_change_location.setOnClickListener(this);
		tv_setbg = (TextView) this.findViewById(R.id.tv_adtools_setbg);

		tv_setbg.setOnClickListener(this); // �������tv_adtools_query�ĵ���¼�����һ��onClick����


		cb_adtools_status_control
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton ButtonView,
							boolean isChecked) {
						if (isChecked) {
							startService(serviceIntent);
							tv_adtools_service_status.setTextColor(Color.WHITE);
							tv_adtools_service_status.setText("�����ط����Ѿ�����");
						} else {
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
		case R.id.tv_adtools_setbg:
			//4.5-2�½�һ��������ѡ�Ի���,�������½�һ��xml����
			AlertDialog.Builder builder= new Builder(this);
			builder.setTitle("��������ʾ���");
			
			String[] items=new String[]{"��͸��","������","ƻ����"};
			//���õ�ѡ����ʾ���
			builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
				 //��ʾָ��OnClickListener������ΪDialogInterface��Ҫ���ᱨ����
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//��ȡһ��sp��editor
					Editor editor=sp.edit();
					editor.putInt("background", which);
					editor.commit();
				}
			});//Ĭ��ѡ����ǵ�һ�����±�Ϊ0
			
			//�ٸ���������һ��ȷ����ť
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			//����builderչ�ֳ���
			builder.show();
			break;
		case R.id.tv_adtools_change_location:
			
			//����һ���ƶ�����
			Intent intent=new Intent(this,DragViewActivity.class);
			startActivity(intent);
			
			break;
		case R.id.tv_adtools_sms_backup:
			//������Ҫ�Ѷ�������ת����xml�ļ�
			
			Intent intent1 = new Intent(this, BackupSmsService.class);
			startService(intent1);
			
			break;
		case R.id.tv_adtools_sms_restore:
			//��ȡxml�ļ�
			//����xml�ļ�����뵽����Ӧ�ó�����	
			//��ֹ��ԭ;�б��û��жϣ���˲��õ����Ի�����ʽ���ó����Լ��رգ��û����ܿ���
			final ProgressDialog pd=new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setCancelable(false);//���ܱ�ȡ��
			pd.setMessage("���ڻ�ԭ����");
			pd.show();//�ѶԻ�����ʾ����
			smsInfoService=new SmsInfoService(this);
			//�Ǹ���ʱ�������������߳�������
			new Thread(){
				//��Thread�б�����дrun������Ҫ��û��ʶThrea��ĺ�������
				@Override
				public void run() {
					super.run();
			System.out.println("huifuzhong");		
					try {
						smsInfoService.restoreSms("/sdcard/smsbackup.xml",pd);
						pd.dismiss();
						//����ɹ��˾͵����ָ��ɹ��Ի���
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "���Żָ����", 1).show();
						Looper.loop();//�ֶ�����ѭһ��
					} catch (Exception e) {
						e.printStackTrace();
						pd.dismiss();
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "���Żָ�ʧ��", 1).show();
						Looper.loop();//�ֶ�����ѭһ��
					}
					
				}
			}.start();
			
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
