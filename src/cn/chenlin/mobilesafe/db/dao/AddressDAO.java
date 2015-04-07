package cn.chenlin.mobilesafe.db.dao;

import android.R.string;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AddressDAO {
	
	/**
	 * @param path  ���ݿ�·��
	 * @return ���ݿ����
	 */

	public static SQLiteDatabase getAddressDB(String path){
		return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}
	
}
