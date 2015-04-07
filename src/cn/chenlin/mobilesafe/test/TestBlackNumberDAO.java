package cn.chenlin.mobilesafe.test;

import java.util.List;

import cn.chenlin.mobilesafe.db.dao.BlackNumberDAO;
import android.R.integer;
import android.test.AndroidTestCase;

//����һ����������������̳�AndroidTestCase
public class TestBlackNumberDAO extends AndroidTestCase {

	public void testADD() throws Exception {
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		long number = 13512345678l; //���ַ���ĩβ����һ����l����ĸ
		for (int i = 0; i < 100; i++) {
			// int�������м����ַ����������ͱ�����ַ������������ִ�
			dao.add(number + i + "");
		}
	}
	
	public void TestFindAllNum() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		List<String> arrList=dao.findAllNumbers();
		System.out.print(arrList.size());
		assertEquals(100, arrList.size());
		
	}
	public void testDelete() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.delete("13512345777");
	}
	
	public void testupdate() throws Exception{
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		dao.update("13512345776", "13512345779");
	}
}
