package cn.chenlin.mobilesafe.domain;

public class ContactInfo {
	private String name;
	private String phone;
	
	//�޲εĹ��췽��
	public ContactInfo(){}
	
	//���ɹ��췽������Ū���޲εĹ��췽��������
	public ContactInfo(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}



}
