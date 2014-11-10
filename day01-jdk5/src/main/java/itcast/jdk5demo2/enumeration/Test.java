package itcast.jdk5demo2.enumeration;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//1.原始方法控制输入 student1
		Student1 s = new Student1();
		//s.setGrade("R");
		
		//2.改进后  student2
		Student2 s2 = new Student2();
		//s2.setGrade(Grade.A);
		
		//3.用enum控制  student3
		/*Student3 s3 = new Student3();
		s3.setGrade2(Grade2.E);  //100-90
		System.out.println(s3.getGrade2().getValue());
		System.out.println(s3.getGrade2().toLocaleString());  //差
		*/
		
		System.out.println(WeekDay.SUN.toLocaleString());
		
		
		
		/********枚举的几个常用方法****/
		System.out.println(WeekDay.SAT.name());  //得到枚举的名称
		System.out.println(WeekDay.SAT.ordinal());  //得到枚举所处的位置
		
		//把字符串转成枚举对象
		String str = "SAT1";
		WeekDay weekday = WeekDay.valueOf(str);  //把字符串转成枚举对象
		System.out.println(weekday.name());
		
		WeekDay wds[] = WeekDay.values();  //得到枚举的所有枚举值
		for(WeekDay wd : wds){
			System.out.println(wd.name());
		}
	}

}
