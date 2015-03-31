package cn.chenlin.mobilesafe.test;

import java.util.List;

import cn.chenlin.mobilesafe.domain.ContactInfo;
import cn.chenlin.mobilesafe.engine.ContactInfoService;
import android.test.AndroidTestCase;

public class TestGetContactInfo extends AndroidTestCase {
	                                       //���쳣�׸����Կ��
	public void  testGetContacts() throws Exception{
		ContactInfoService service=new ContactInfoService(getContext());//����һ��������
		List<ContactInfo> infos=service.getContactInfos();
		//�õ��������ϵ����Ϣ��ʾ����
		for(ContactInfo info:infos){
			System.out.println(info.getName());
			System.out.println(info.getPhone());			
		}
	}
}
