package map;
/*
map扩展知识--集合嵌套。

map集合被使用是因为具备映射关系。

"yureban" Student("01" "zhangsan");
"yureban" Student("02" "lisi");

"jiuyeban" Student("01" "wangwu");
"jiuyeban" Student("02" "zhaoliu");

HashMap<String,List<Student>> czbk：
一个学校有多个教室(每一个教室都有名称),每个教室中有多个学生(这些学生连成一条链表)

一对多映射
 */
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
public class MapNestingList {
	public static void main(String[] args){
		//创建学校
		HashMap<String , List<Student>> bupt = new HashMap<String , List<Student>>();
		//创建教室
		ArrayList<Student> wangyan = new ArrayList<Student>();
		List<Student> jisuanji = new ArrayList<Student>();
		//往教室中添加学生
		wangyan.add(new Student("sansan" , 12));
		wangyan.add(new Student("sisi" , 32));
		jisuanji.add(new Student("caozei" , 12));
		jisuanji.add(new Student("liuliu" , 32));
		//往bupt中添加教室
		bupt.put("wangyan", wangyan);
		bupt.put("jisuanji", jisuanji);
		
		//访问所有的学生
		Collection<Map.Entry<String , List<Student>>> coll = bupt.entrySet();
		Iterator<Map.Entry<String , List<Student>>> it = coll.iterator();
		while(it.hasNext()){
			Map.Entry<String , List<Student>> entry = it.next();
			String roomKey = entry.getKey();//取出教室名称
			List<Student> roomValue = entry.getValue();//取出该教室的所以学生(一个链表)
			getStudentInfo(roomValue);
		}
	}
	
	public static void getStudentInfo(List<Student> room){
		Iterator<Student> it = room.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	
}

class Student{
	private String name;
	private int age;
	public Student(String name , int age){
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public String toString(){
		return this.name + "..." + this.age;
	}
}
