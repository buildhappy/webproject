package itcast.jdk5demo2.enumeration;

public  enum WeekDay {
	
	//创建对象的时候进行赋初值
	MON("星期一"),TUE("星期二"),WED("星期三"),THU("星期四"),FRI("星期二"),SAT("星期二"),SUN("星期二");  //toLocaleString
	private String localeString;
	private WeekDay(String localeString){
		this.localeString = localeString;
	}
	public String toLocaleString(){
		return localeString;
	}
}
