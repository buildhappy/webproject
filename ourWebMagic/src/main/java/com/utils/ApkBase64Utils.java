package com.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ApkBase64Utils {
	public static String GetApkStr(byte[] date){
		BASE64Encoder encoder=new BASE64Encoder();
		return encoder.encode(date);
	}
	public static boolean GenerateApk(String path,String  filename,String apkStr) {
		System.out.println("apkStr == null"+apkStr == null);
		if(apkStr==null){
			return false ;
		}
		//BASE64Encoder encoder = new BASE64Encoder() ;
		BASE64Decoder decoder = new BASE64Decoder();
		
		
		try{
			byte[] b = decoder.decodeBuffer(apkStr);
			for(int i =0 ;i<b.length;i++){
				if(b[i]<0){
					b[i]+=256 ;
				}
			String apkFilePath = path+filename ;
			OutputStream out =  new FileOutputStream(apkFilePath);
			out.write(b);
			out.flush();
			out.close();
			
			}
		}catch(Exception e){
			System.out.println("");
			return false ;
		}
		return true;
		
	}
}
