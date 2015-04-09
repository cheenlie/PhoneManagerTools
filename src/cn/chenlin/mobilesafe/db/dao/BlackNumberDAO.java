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
	//�����������Ҫʹ��context
	private Context context;
	//�ڹ��캯�����������ʼ��
	private SQLiteOpenHelper sqLiteOpenHelper;

	public BlackNumberDAO(Context context) {
		this.context = context;
		//��ʼ��sqLiteOpenHelper��û�����ݿ����ᴴ��һ�����ݿ�
		sqLiteOpenHelper=new BlackNumberDBHelper(context);
	}

	/**
	 * ���Һ���
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
	 * ���뺯��
	 */
	public void add(String number){
		//ԭ�����ݿ����ѽ����������¼�Ͳ��ò�����
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
	 * ɾ��
	 */
	public void delete(String number){

		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("delete from blacknumber where number=?", new Object[]{number});
			db.close();
		}
	}
	
	/**
	 * ���²���
	 * @param oldnumber �ɵĺ���
	 * @param newNumber �µĺ���
	 */
	public void  update(String oldnumber ,String newNumber){
		SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
		if(db.isOpen()){
			db.execSQL("update blacknumber set number=? where number=?  ", new Object[]{newNumber,oldnumber});
			db.close();
		}
	}
	
	/**
	 * ���ȫ������
	 */
	
	public List<String> findAllNumbers(){
		SQLiteDatabase db=sqLiteOpenHelper.getReadableDatabase();
		List<String> numbers=new ArrayList<String>();  //��仰�Ķ��壬��ס
		if(db.isOpen()){
//			��ѯ��rawquery��������������仰
//			db.execSQL("select * from blacknumber");
			Cursor cursor= db.rawQuery("select number from blacknumber", null); //�ڶ���������û�е�ʱ��ͷ���Ϊnull
			//ֻ�ܵ����������������ݣ�����������cursor��ʹ��
			while (cursor.moveToNext()) {
				//����ÿһ������ôȡ����������
				String number=cursor.getString(0);//�õ���0����Ϣ����һ����
				//Ȼ������number�ŵ�numbers����
				numbers.add(number);
			}
			cursor.close();
			db.close();
		}
		return numbers;
	}
}
