package ch05InitClean;
/**
 * enum类的使用
 * @author buildhappy
 *
 */
public class P106Enum {
	public static void main(String[] args){
		//enum事先定义的三个函数：
		//toString()，自动重写该方法，便于取出枚举的值
		//ordinal()，表名某个特定enum常量的声明顺序
		//values()
		for(Spiciness sp : Spiciness.values()){
			System.out.println(sp + " " + sp.ordinal());
		}
	}
}

enum Spiciness{
	NOT , MILD , MEDIUM , HOT , FLAMING
}
