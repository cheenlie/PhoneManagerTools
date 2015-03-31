package cn.chenlin.mobilesafe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
	
	//这儿永远不会产生一个NoSuchAlgorithmException异常，所以我们用try catch直接抛出异常
	public static String enCode(String pwd) {
		try {
			MessageDigest  digest=MessageDigest.getInstance("MD5");
			byte[] bytes=digest.digest(pwd.getBytes());
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<bytes.length;i++){
				String s=Integer.toHexString(0xff&bytes[i]);
				if(s.length()==1){
					sb.append("0"+s);
				}else {
					sb.append(s);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("buhuifashen");//不会发生异常
		}
	}

}
