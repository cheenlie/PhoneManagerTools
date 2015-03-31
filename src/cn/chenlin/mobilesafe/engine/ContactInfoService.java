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
	//������ϣ��new������ʱ��ͳ�ʼ��context����ô�����������Ĺ��캯��
	public ContactInfoService(Context context) {
		this.context = context;
	}
	
	//���صľ���һ������list,�����ŵľ���ContactInfo,������ΪgetContactInfos
	public List<ContactInfo> getContactInfos(){
		
		//����һ�����ϣ��������Ϣ
		List<ContactInfo> infos=new ArrayList<ContactInfo>();
		ContactInfo info;
		
		//Ҫ����ϵͳ�ṩ���ṩϵͳ��Ϣ���Ǿͱ�����������
		ContentResolver resolver=context.getContentResolver();
		
		//�õ�resolver�����ǾͿ���ȥ�����Ϣ��
		//1. ��ȡ��ϵ�˵�id
		//2. ������ϵ�˵�id�����ϵ�˵�����
		//3. ������ϵ�˵�id ���ݵ�type��ȡ��Ӧ�����ݣ��绰��email��
		Uri uri=Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri=Uri.parse("content://com.android.contacts/data");
		//ȡ�������,�õ������˵�id
		Cursor cursor= resolver.query(uri, null, null, null, null);
		
		//�����ƶ��͵���ĩβ��
		while (cursor.moveToNext()) {
			//ǰ��info��û��ֵ
			info=new ContactInfo();
			//��ȡ��ϵ��id
			String id=cursor.getString(cursor.getColumnIndex("_id"));
			//��ȡ��ϵ������
			String name=cursor.getString(cursor.getColumnIndex("display_name"));
			info.setName(name);//�����ַŽ�ȥ
			
			//Ҫ��ѵ绰����ȡ�����ͱ������data��
			Cursor	datacursor=resolver.query(datauri,
									null, //	���ж���ʾ������
									"raw_contact_id=?", //��ʾ�����������ʺ�
									new String[]{id}, //��ʾ���ݣ���ʾ����һ��string���飬
									null   //������������Ҫ����
									);
			while (datacursor.moveToNext()) {
					//��ȡ����
				String type=datacursor.getString(datacursor.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/phone_v2".equals(type)) {   //�����Ͷ�Ӧ�Ĳ��ǵ绰����
					String phonenumber=datacursor.getString(datacursor.getColumnIndex("data1"));//��ȡdata1��һ�е�����
					info.setPhone(phonenumber);
				}
			}
			datacursor.close();// ������Ҫ�ر�
			infos.add(info);
			//Ϊ�����������info�Ž�ȥ�����info
			info=null;
			
		}
		cursor.close();
		return infos;
	}

}