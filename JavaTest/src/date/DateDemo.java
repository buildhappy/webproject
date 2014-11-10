package date;

import java.util.Date;

import java.text.SimpleDateFormat;
public class DateDemo {
	public static void main(String[] args){
		//创建日期对象
		Date date = new Date();
		System.out.println(date);
		
		//
		SimpleDateFormat smf = new SimpleDateFormat("yy.M.d D");
		String time = smf.format(date);
		System.out.println(time);
	}
}
