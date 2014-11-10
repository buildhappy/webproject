package itcast.jdk5demo2.enumeration;

public class Student3 {
	private String name;
	private Grade2 grade2;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Grade2 getGrade2() {
		return grade2;
	}
	public void setGrade2(Grade2 grade2) {
		this.grade2 = grade2;
	}
	
	
}

enum Grade2{  //class
	A("100-90"){//创建对象的时候进行赋初值
		public  String toLocaleString(){
			return "优";
		}
	}
	,
	B("89-80"){
		public  String toLocaleString(){
			return "良";
		}
	}
	,
	C("79-70"){
		public  String toLocaleString(){
			return "好";
		}
	}
	,
	D("69-60"){
		public  String toLocaleString(){
			return "差";
		}
	}
	,
	E("59-0"){
		public  String toLocaleString(){
			return "不及格";
		}
	};
	
	private String value;  //优、良、好、差、不及格
	private Grade2(String value){
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public abstract String toLocaleString();
}

/*以下代码与enum Grade2{A,B,C,D,E}等价
 class Grade{
	private Grade(){}
	public static Grade A = new Grade();
	public static Grade B = new Grade();
	public static Grade C = new Grade();
	public static Grade D = new Grade();
	public static Grade E = new Grade();
}
 */

enum T{  //class T   A
	A;
}
