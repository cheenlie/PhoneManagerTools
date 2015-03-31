package cn.chenlin.mobilesafe.test;

import cn.chenlin.mobilesafe.R;
import cn.chenlin.mobilesafe.domain.UpdateInfo;
import cn.chenlin.mobilesafe.engine.UpdateInfoService;
import android.test.AndroidTestCase;

public class TestGetUpdateInfo extends AndroidTestCase {
	
	public void testUpdateInfo() throws Exception{
		
		UpdateInfoService service=new UpdateInfoService(getContext());		
		UpdateInfo info=service.getUpdateInfo(R.string.updateurl);		
		assertEquals("2.0", info.getVersion());
	}

}
