package cn.chenlin.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//android�ﴴ��һ�����ݿ�
public class BlackNumberDBHelper extends SQLiteOpenHelper{

	//������û�ж��幹�캯������ô�Ӻ����ͱ�����ʾ�Ķ��幹�캯��
	public BlackNumberDBHelper(Context context) {
		//����һ�����ݿ�
		super(context, "blackname.db", null, 1);
	}

	/**
	 * ��һ�δ������ݿ��ʱ�����oncreate����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE blacknumber (_id integer primary key autoincrement, number varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
}
