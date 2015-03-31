package cn.chenlin.mobilesafe.ui;

import java.util.List;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.util.MD5Encoder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class AntivirusActivity extends Activity implements OnClickListener {

	protected static final int STOP = 100;
	private ImageView iv;
	private SQLiteDatabase db;
	private ProgressBar pb;
	private LinearLayout ll;
	private Button ok_bt;
	private AnimationDrawable ad;
	private ScrollView sv;// ʹ��ʾ������textView�����¹����鿴
	private boolean flagScanning = false;

	// ���̣߳�����Ҫ��handler��message
	private Handler handler = new Handler() {
		// new�����������дhandler�ķ���
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == STOP) { // �����������Ϣ��ֹͣ������
				ll.removeAllViews();// ɱ����ɺ�����е�view��remove��
				ad.stop();
				ok_bt.setVisibility(View.VISIBLE);
			}
			String str = (String) msg.obj;// �õ��̴߳���������Ϣ����
			if (str != null) {
				// �õ����ݺ��������ʾ����������
				TextView tv = new TextView(getApplicationContext());// ����һ��textview
				tv.setText(str);
				// ��tv��������ʾ��ll��
				ll.addView(tv);
			}
			sv.scrollBy(0, 20);// ����ˮƽ���򲻶�����ֱ�����ƶ���ʮ������

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.antivirus_main);

		// �����ݿ� ,��ֻ����ʽ�򿪣������Ҫ�ص����ݿ⣬��ondestory�����йص��������Ҫ��дondestory����
		db = SQLiteDatabase.openDatabase("/sdcard/antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);

		iv = (ImageView) this.findViewById(R.id.iv);
		pb = (ProgressBar) this.findViewById(R.id.progressBar1);
		ll = (LinearLayout) this.findViewById(R.id.ll);
		sv = (ScrollView) this.findViewById(R.id.sv);
		ok_bt = (Button) this.findViewById(R.id.antivirus_end_ok_bt);

		// ��֡����
		iv.setBackgroundResource(R.drawable.anti_anim);
		// �Ѷ������ŷ��ڱ�������
		ad = (AnimationDrawable) iv.getBackground();

		ok_bt.setOnClickListener(this);
	}

	// ���õ��һ����Ļ�Ϳ�ʼɱ��

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ֻ���ڵ����һ����Ļ��ʱ���ɨ���ֻ�������Ҫ��һ��flag���ж�
		if (!flagScanning) {
			// onTouchEvent�¼�������갴�ºͷſ���ʱ��һ���������¼�������Ҫ���ڵ����Ǵ��¼�������ɨ��
			if (event.getAction() == MotionEvent.ACTION_UP) {
				flagScanning = true;// һ�����͸ı��־λ��ʹ���ܵڶ��ν���ɨ���¼�

				// �ȰѶ���Ч����������
				ad.start();

				// �½�һ�����߳�
				new Thread() {
					// �½��̺߳���дrun����
					public void run() {
						// Ҫɱ��Ҫ�ȱ��������г���ļ���
						// ��ȡ����װ���ֻ��ϵ����г���(��װ��δ��װ�ĳ���),���ذ�װ������Ϣ
						// ����PackageManager.GET_SIGNATURES����ʾ�ĸ���Ҫ��ȡӦ�ó������Ϣ
						List<PackageInfo> infos = getPackageManager()
								.getInstalledPackages(
										PackageManager.GET_UNINSTALLED_PACKAGES
												| PackageManager.GET_SIGNATURES);
						// ���ÿһ��Ӧ�ó����ǩ����Ȼ������ݿ��еĶԱȣ���������ݿ�����ʾ�Ƿ��Ǹ�����
						// ��һ����ʱ�̣߳���˽���һ�����̣߳�����Χ���thread�߳�

						// ���ý�����
						pb.setMax(infos.size());

						int total = 0;// ��¼ɨ��������Ա���ʾɨ����ȡ�
						int virustotal = 0;// ��¼��������
						Message msg_virus = null;
						// ��ȡÿ��Ӧ�ó����ǩ����
						// ����ǿ��forѭ��,��infos��������ǿ��forѭ��
						for (PackageInfo info : infos) {

							total++;

							Message msg = Message.obtain();// ��ȡ��һ����Ϣ
							msg.obj = "����ɨ��" + info.packageName;
							handler.sendMessage(msg);
							System.out.println(info.packageName);
							// �õ�Ӧ�ó����ǩ��,���ص��Ǹ�����
							Signature[] signs = info.signatures;
							// System.out.println(signs[0].toCharsString());
							// ÿ��Ӧ�ó����ǩ������һ����ÿ�������߿����ĳ���Ҳ�Ǹ��������е�
							// ǩ��̫���ˣ���md5ת�룬ת���ɹ̶����ȡ�
							String string = signs[0].toCharsString();
							// ��ǩ��md5һ�£��õ�һ��md5ֵ
							String md5 = MD5Encoder.enCode(string);

							// if("3fbff420f6a9247626e83b764dee49f5".equals(md5)||"884038af7715538c7b22ea5905edeaba".equals(md5))
							// {
							// System.err.println("------");
							// //��ӡ��ɫ�Ŀ������Ƚ�����
							// }

							// Ȼ��ͺ����ݿ���������ݶԱȣ��Ա�ǰ����oncreat����������ݿ�
							Cursor cursor = db.rawQuery(
									"select desc from datable where md5=?",
									new String[] { md5 });
							// ����������Ϊ�գ����в���
							if (cursor.moveToFirst()) {
								// �õ�����������
								String desc = cursor.getString(0);
								// �õ�������Ϣ��Ϳ��԰���Ϣ���ظ����߳�
								msg = Message.obtain();// ��ȡ��һ����Ϣ
								msg.obj = info.packageName + ":" + desc;
								// Ȼ��������Ϣ���ͳ�ȥ
								handler.sendMessage(msg);

								msg_virus = Message.obtain();
								msg_virus.obj = info.packageName + " : " + desc;

								virustotal++;
							}
							cursor.close();

							// ѭ��������ʱ������½�����
							pb.setProgress(total);
						}

						// ɨ����Ϸ��ز�����ٲ���
						Message msg = Message.obtain();// ��ȡ��һ����Ϣ
						msg.what = STOP;// ����һ����־�����̣߳������߳�ͣ��������
						msg.obj = "ɨ����Ϲ�����" + virustotal + "������";
						handler.sendMessage(msg);
						handler.sendMessage(msg_virus);
						// ɨ����ϣ����������㣬��־λ�ı��Ա�ڶ��β���
						flagScanning = false;
						pb.setProgress(0);
					};
				}.start();
			}
		} else {
			return false;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		// Ϊ�˲��ó��򱨴������ж������ݿ�״̬���򿪵�ʱ��Źر����ݿ�
		if (db.isOpen()) {
			// �˳����activity��ʱ��ص����ݿ�
			db.close();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.antivirus_end_ok_bt:
			finish();
			// Intent mainIntent=new Intent(AntivirusActivity.this ,
			// MainActivity.this);
			// startActivity(mainIntent);
			break;
		}
	}

}
