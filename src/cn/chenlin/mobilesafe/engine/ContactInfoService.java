package cn.chenlin.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.chenlin.mobilesafe.domain.ContactInfo;


public class ContactInfoService {
	
	private Context context;
	//当我们希望new这个类的时候就初始化context，那么我们重新它的构造函数
	public ContactInfoService(Context context) {
		this.context = context;
	}
	
	//返回的就是一个集合list,里面存放的就是ContactInfo,方法名为getContactInfos
	public List<ContactInfo> getContactInfos(){
		
		//设置一个集合，来存放信息
		List<ContactInfo> infos=new ArrayList<ContactInfo>();
		ContactInfo info;
		
		//要利用系统提供者提供系统信息，那就必须获得上下文
		ContentResolver resolver=context.getContentResolver();
		
		//得到resolver后我们就可以去获得信息啦
		//1. 获取联系人的id
		//2. 根据联系人的id获得联系人的名字
		//3. 根据联系人的id 数据的type获取对应的数据（电话，email）
		Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri=Uri.parse("content://com.android.contacts/data");
		//取到结果集,得到所有人的id
		Cursor cursor= resolver.query(uri, null, null, null, null);
		
		//不能移动就到了末尾啦
		while (cursor.moveToNext()) {
			//前面info还没赋值
			info=new ContactInfo();
			//获取联系人id
			String id=cursor.getString(cursor.getColumnIndex("_id"));
			//获取联系人名字
			String name=cursor.getString(cursor.getColumnIndex("display_name"));
			info.setName(name);//把名字放进去
			
			//要想把电话号码取出来就必须操作data表
			Cursor	datacursor=resolver.query(datauri,
									null, //	所有都显示出来，
									"raw_contact_id=?", //显示条件：等于问号
									new String[]{id}, //显示内容：显示的是一个string数组，
									null   //排序条件：不要排序
									);
			while (datacursor.moveToNext()) {
					//获取类型
				String type=datacursor.getString(datacursor.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/phone_v2".equals(type)) {   //这类型对应的才是电话号码
					String phonenumber=datacursor.getString(datacursor.getColumnIndex("data1"));//获取data1这一列的数据
					info.setPhone(phonenumber);
				}
			}
			datacursor.close();// 用完了要关闭
			infos.add(info);
			//为保险起见，把info放进去后，清空info
			info=null;
			
		}
		cursor.close();
		return infos;
	}

}