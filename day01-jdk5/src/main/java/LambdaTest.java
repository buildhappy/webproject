/**
 * jdk1.8中的lambda编程
 * @author Administrator
 *
 */
public class LambdaTest{
	public static void main(String[] args){
		//HelloMessage sayHello = (String ms)->{System.out.println(ms);}
		//sayHello.hello("in hello");
	}
}
interface HelloMessage{
	void hello(String mes);
}