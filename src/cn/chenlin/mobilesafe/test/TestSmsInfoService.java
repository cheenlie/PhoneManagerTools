package cn.chenlin.mobilesafe.test;

import java.util.List;

import cn.chenlin.mobilesafe.domain.SmsInfo;
import cn.chenlin.mobilesafe.service.SmsInfoService;
import android.test.AndroidTestCase;

public class TestSmsInfoService extends AndroidTestCase {

	public void testSmsInfoService() throws Exception{
		SmsInfoService service=new SmsInfoService(getContext());
		List<SmsInfo> smsInfos=service.getSmsInfo();
		assertEquals(1, smsInfos.size());
	}
}
