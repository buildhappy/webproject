package reflect;
/**
 * 通过放射来实例化对象
 * 原类中有无参的构造函数
 * @author Administrator
 *
 */
import java.lang.reflect.Constructor;
public class Demo2 {
	public static void main(String[] args)throws Exception{
		Class<?> c = null;
		c = Class.forName("reflect.Chinese");//实例化c
		Constructor<?>[] cons = null;
		cons = c.getConstructors();
		
		Chinese p = null;
		p = (Chinese)cons[0].newInstance("jack" , "male");
		p.setName("jack");
		p.setSex("male");
		System.out.println(p);
		
	}
}

class Chinese{
	private String name ;
	private String sex;
	public Chinese(String name , String sex){
		this.name = name;
		this.sex = sex;
	}
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