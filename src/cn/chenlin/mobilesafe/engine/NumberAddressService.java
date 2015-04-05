package cn.chenlin.mobilesafe.engine;

import cn.chenlin.mobilesafe.dao.AddressDAO;
import android.R.integer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressService {

	/**
	 * 
	 * @param number
	 *            电话号码
	 * @return 号码归属地
	 */
	public static String getNumberAddress(String number) {
		String parten = "^1[3458]\\d{9}$";
		String address = number;
		SQLiteDatabase db;
		// 手机号
		if (number.matches(parten)) {
			db = AddressDAO.getAddressDB("/sdcard/address.db");
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery(
						"select city from info where mobileprefix=?",
						new String[] { number.substring(0, 7) });
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
				db.close();
			}
			// 座机号
		} else {
			int length = number.length();
			switch (length) {
			case 4:
				address = "模拟器号码";
				break;
			case 7:
				address = "本地号码";
				break;
			case 8:
				address = "本地号码";
				break;
			case 10: // 010+7位
				// select city from info where area='010' limit 1
				db = AddressDAO.getAddressDB("/sdcard/address.db");
				if (db.isOpen()) {
					Cursor cursor = db.rawQuery(
							"select city from info where area=? limit 1",
							new String[] { number.substring(0, 3) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
					db.close();
				}
				break;
			case 11: // 0832+7位 或者 010+8位
				db = AddressDAO.getAddressDB("/sdcard/address.db");
				if (db.isOpen()) {
					Cursor cursor1 = db.rawQuery(
							"select city from info where area=? limit 1",
							new String[] { number.substring(0, 4) });
					if (cursor1.moveToNext()) {
						address = cursor1.getString(0);
					}
					Cursor cursor2 = db.rawQuery(
							"select city from info where area=? limit 1",
							new String[] { number.substring(0, 3) });
					if (cursor2.moveToNext()) {
						address = cursor2.getString(0);
					}
					cursor1.close();
					cursor2.close();
					db.close();

				}
				break;
			case 12: // 0832+ 8位
				db = AddressDAO.getAddressDB("/sdcard/address.db");
				if (db.isOpen()) {
					Cursor cursor3 = db.rawQuery(
							"select city from info where area=? limit 1",
							new String[] { number.substring(0, 4) });
					if (cursor3.moveToNext()) {
						address = cursor3.getString(0);
					}
					cursor3.close();
					db.close();
				}
				break;
			}
		}
		return address;
	}
}
