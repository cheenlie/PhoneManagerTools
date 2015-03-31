package cn.chenlin.mobilesafe.domain;

public class ContactInfo {
	private String name;
	private String phone;
	
	//无参的构造方法
	public ContactInfo(){}
	
	//生成构造方法，再弄个无参的构造方法，如上
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
