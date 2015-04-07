package cn.chenlin.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//android里创建一个数据库
public class BlackNumberDBHelper extends SQLiteOpenHelper{

	//父函数没有定义构造函数，那么子函数就必须显示的定义构造函数
	public BlackNumberDBHelper(Context context) {
		//创建一个数据库
		super(context, "blackname.db", null, 1);
	}

	/**
	 * 第一次创建数据库的时候调用oncreate方法
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
