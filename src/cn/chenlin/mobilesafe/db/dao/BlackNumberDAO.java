package cn.chenlin.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import cn.chenlin.mobilesafe.db.BlackNumberDBHelper;
import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlackNumberDAO {

	private static final String TAG = "BlackNumberDAO";
	//在这个类里面要使用context
	private Context context;
	//在构造函数里面把它初始化
	private SQLiteOpenHelper sqLiteOpenHelper;

	public BlackNumberDAO(Context context) {
		this.context = context;
		//初始化sqLiteOpenHelper，没有数据库它会创建一个数据库
		sqLiteOpenHelper=new BlackNumberDBHelper(context);
	}

	/**
	 * 查找函数
	 */
	public boolean find(String number) {
		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
		boolean result=false;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select number from blacknumber where number=?",
					new String[] { number });
			if (cursor.moveToNext()) {
				result=true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}
	
	/**
	 * 插入函数
	 */
	public void add(String number){
		//原来数据库中已结有了这个记录就不用插入了
		if(find(number)){
			return;
		}
		SQLiteDatabase db=sqLiteOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("insert into blacknumber (number) values (?)", new Object[] {number});
			db.close();
		}
	}
	
	/**
	 * 删除
	 */
	public void delete(String number){

		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from blacknumber where number=?", new Object[]{number});
			db.close();
		}
	}
	
	/**
	 * 更新操作
	 * @param oldnumber 旧的号码
	 * @param newNumber 新的号码
	 */
	public void  update(String oldnumber ,String newNumber){
		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("update blacknumber set number=? where number=?  ", new Object[]{newNumber,oldnumber});
			db.close();
		}
	}
	
	/**
	 * 获得全部号码
	 */
	
	public List<String> findAllNumbers(){
		SQLiteDatabase db=sqLiteOpenHelper.getReadableDatabase();
		List<String> numbers=new ArrayList<String>();  //这句话的定义，记住
		if(db.isOpen()){
//			查询用rawquery，而不是下面这句话
//			db.execSQL("select * from blacknumber");
			Cursor cursor= db.rawQuery("select number from blacknumber", null); //第二个参数，没有的时候就放置为null
			//只能单个遍历产生的数据，而不能整个cursor的使用
			while (cursor.moveToNext()) {
				//遍历每一个后怎么取出其中数据
				String number=cursor.getString(0);//拿到第0列信息，是一个数
				//然后把这个number放到numbers里面
				numbers.add(number);
			}
			cursor.close();
			db.close();
		}
		return numbers;
	}
}
