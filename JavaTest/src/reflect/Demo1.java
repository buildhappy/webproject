package reflect;
/**
 * 通过放射来实例化对象
 * 原类中没有无参的构造函数
 * @author Administrator
 *
 */
public class Demo1 {
	public static void main(String[] args)throws Exception{
		Class<?> c = null;
		c = Class.forName("reflect.Person");//法1,实例化c
		c = Person.class;//法2
		c = c.getClass();
		Person p = null;
		p = (Person)c.newInstance();
		p.setName("jack");
		p.setSex("male");
		System.out.println(p);
		
	}
}

class Person{
	private String name ;
	private String sex;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String toString(){
		return "name:" + name +"  sex:" + sex;
	}
	
}