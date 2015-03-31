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
	private ScrollView sv;// 使显示出来的textView能上下滚动查看
	private boolean flagScanning = false;

	// 子线程，必须要有handler和message
	private Handler handler = new Handler() {
		// new出来后必须重写handler的方法
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == STOP) { // 如果是最后个消息就停止掉动画
				ll.removeAllViews();// 杀毒完成后把所有的view都remove掉
				ad.stop();
				ok_bt.setVisibility(View.VISIBLE);
			}
			String str = (String) msg.obj;// 拿到线程传回来的信息内容
			if (str != null) {
				// 拿到内容后把内容显示到主界面上
				TextView tv = new TextView(getApplicationContext());// 设置一个textview
				tv.setText(str);
				// 把tv的内容显示到ll上
				ll.addView(tv);
			}
			sv.scrollBy(0, 20);// 让他水平方向不懂，垂直方向移动二十个像素

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.antivirus_main);

		// 打开数据库 ,以只读方式打开，用完后要关掉数据库，在ondestory方法中关掉它，因此要重写ondestory方法
		db = SQLiteDatabase.openDatabase("/sdcard/antivirus.db", null,
				SQLiteDatabase.OPEN_READONLY);

		iv = (ImageView) this.findViewById(R.id.iv);
		pb = (ProgressBar) this.findViewById(R.id.progressBar1);
		ll = (LinearLayout) this.findViewById(R.id.ll);
		sv = (ScrollView) this.findViewById(R.id.sv);
		ok_bt = (Button) this.findViewById(R.id.antivirus_end_ok_bt);

		// 做帧动画
		iv.setBackgroundResource(R.drawable.anti_anim);
		// 把动画播放放在背景播放
		ad = (AnimationDrawable) iv.getBackground();

		ok_bt.setOnClickListener(this);
	}

	// 设置点击一下屏幕就开始杀毒

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 只能在点击第一次屏幕的时候才扫描手机，所以要用一个flag来判断
		if (!flagScanning) {
			// onTouchEvent事件，在鼠标按下和放开的时候一共是两次事件，所以要绑定在弹起那次事件才启动扫描
			if (event.getAction() == MotionEvent.ACTION_UP) {
				flagScanning = true;// 一进来就改变标志位，使不能第二次进入扫描事件

				// 先把动画效果启动起来
				ad.start();

				// 新建一个子线程
				new Thread() {
					// 新建线程后重写run方法
					public void run() {
						// 要杀毒要先遍历出所有程序的集合
						// 获取到安装在手机上的所有程序(安装和未安装的程序),返回安装包的信息
						// 加上PackageManager.GET_SIGNATURES，显示的告诉要获取应用程序的信息
						List<PackageInfo> infos = getPackageManager()
								.getInstalledPackages(
										PackageManager.GET_UNINSTALLED_PACKAGES
												| PackageManager.GET_SIGNATURES);
						// 获得每一个应用程序的签名，然后和数据库中的对比，如果再数据库里提示是否是个病毒
						// 是一个耗时线程，因此建立一个子线程，如外围这个thread线程

						// 设置进度条
						pb.setMax(infos.size());

						int total = 0;// 记录扫描个数，以便显示扫描进度。
						int virustotal = 0;// 记录病毒个数
						Message msg_virus = null;
						// 获取每个应用程序的签名了
						// 用增强的for循环,在infos里面做增强的for循环
						for (PackageInfo info : infos) {

							total++;

							Message msg = Message.obtain();// 获取到一个消息
							msg.obj = "正在扫描" + info.packageName;
							handler.sendMessage(msg);
							System.out.println(info.packageName);
							// 拿到应用程序的签名,返回的是个数组
							Signature[] signs = info.signatures;
							// System.out.println(signs[0].toCharsString());
							// 每个应用程序的签名都不一样，每个开发者开发的程序也是各自所特有的
							// 签名太长了，用md5转码，转换成固定长度。
							String string = signs[0].toCharsString();
							// 把签名md5一下，得到一个md5值
							String md5 = MD5Encoder.enCode(string);

							// if("3fbff420f6a9247626e83b764dee49f5".equals(md5)||"884038af7715538c7b22ea5905edeaba".equals(md5))
							// {
							// System.err.println("------");
							// //打印红色的看起来比较明显
							// }

							// 然后就和数据库里面的数据对比，对比前先在oncreat方法里打开数据库
							Cursor cursor = db.rawQuery(
									"select desc from datable where md5=?",
									new String[] { md5 });
							// 加入结果集不为空，就有病毒
							if (cursor.moveToFirst()) {
								// 拿到病毒的描述
								String desc = cursor.getString(0);
								// 拿到描述信息后就可以把信息返回给主线程
								msg = Message.obtain();// 获取到一个消息
								msg.obj = info.packageName + ":" + desc;
								// 然后把这个消息发送出去
								handler.sendMessage(msg);

								msg_virus = Message.obtain();
								msg_virus.obj = info.packageName + " : " + desc;

								virustotal++;
							}
							cursor.close();

							// 循环结束的时候更新下进度条
							pb.setProgress(total);
						}

						// 扫描完毕返回查出多少病毒
						Message msg = Message.obtain();// 获取到一个消息
						msg.what = STOP;// 返回一个标志到主线程，让主线程停掉动画。
						msg.obj = "扫描完毕共发现" + virustotal + "个病毒";
						handler.sendMessage(msg);
						handler.sendMessage(msg_virus);
						// 扫描完毕，进度条清零，标志位改变以便第二次操作
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
		// 为了不让程序报错，我们判断下数据库状态，打开的时候才关闭数据库
		if (db.isOpen()) {
			// 退出这个activity的时候关掉数据库
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
