package cn.chenlin.mobilesafe.test;

import java.util.List;

import cn.chenlin.mobilesafe.domain.ContactInfo;
import cn.chenlin.mobilesafe.engine.ContactInfoService;
import android.test.AndroidTestCase;

public class TestGetContactInfo extends AndroidTestCase {
	                                       //把异常抛给测试框架
	public void  testGetContacts() throws Exception{
		ContactInfoService service=new ContactInfoService(getContext());//接收一个上下文
		List<ContactInfo> infos=service.getContactInfos();
		//拿到后遍历联系人信息显示出来
		for(ContactInfo info:infos){
			System.out.println(info.getName());
			System.out.println(info.getPhone());			
		}
	}
}
