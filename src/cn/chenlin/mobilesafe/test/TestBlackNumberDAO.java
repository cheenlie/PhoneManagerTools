package cn.chenlin.mobilesafe.test;

import java.util.List;

import cn.chenlin.mobilesafe.db.dao.BlackNumberDAO;
import android.R.integer;
import android.test.AndroidTestCase;

//创建一个测试用例，必须继承AndroidTestCase
public class TestBlackNumberDAO extends AndroidTestCase {

	public void testADD() throws Exception {
		BlackNumberDAO dao = new BlackNumberDAO(getContext());
		long number = 13512345678l; //长字符串末尾加了一个“l”字母
		for (int i = 0; i < 100; i++) {
			// int型数据中加入字符，整个串就变成了字符串而不是数字串
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
