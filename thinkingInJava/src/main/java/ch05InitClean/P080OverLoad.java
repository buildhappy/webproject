package ch05InitClean;
/**
 * 基本类型的重载
 * @author buildhappy
 *
 */
public class P080OverLoad {
	void f1(char x){System.out.print("f1(char)");}
	void f1(byte x){System.out.print("f1(byte)");}
	void f1(short x){System.out.print("f1(short)");}
	void f1(int x){System.out.print("f1(int)");}
	void f1(long x){System.out.print("f1(long)");}
	void f1(float x){System.out.print("f1(float)");}
	void f1(double x){System.out.print("f1(double)");}
	
	void f2(byte x){System.out.print("f2(byte)");}
	void f2(short x){System.out.print("f2(short)");}
	void f2(int x){System.out.print("f2(int)");}
	void f2(long x){System.out.print("f2(long)");}
	void f2(float x){System.out.print("f2(float)");}
	void f2(double x){System.out.print("f2(double)");}
	
	void f3(short x){System.out.print("f3(short)");}
	void f3(int x){System.out.print("f3(int)");}
	void f3(long x){System.out.print("f3(long)");}
	void f3(float x){System.out.print("f3(float)");}
	void f3(double x){System.out.print("f3(double)");}
	
	void f4(int x){System.out.print("f4(int)");}
	void f4(long x){System.out.print("f4(long)");}
	void f4(float x){System.out.print("f4(float)");}
	void f4(double x){System.out.print("f4(double)");}
	
	void f5(long x){System.out.print("f5(long)");}
	void f5(float x){System.out.print("f5(float)");}
	void f5(double x){System.out.print("f5(double)");}
	
	void f6(float x){System.out.print("f6(float)");}
	void f6(double x){System.out.print("f6(double)");}
	
	void f7(double x){System.out.print("f7(double)");}
	
	void testConstVal(){
		System.out.print("5:");
		f1(5);f2(5);f3(5);f4(5);f5(5);f6(5);f7(5);
		System.out.println();
	}
	
	void testChar(){
		char x = 'x';
		System.out.print("char:");
		f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
		System.out.println();
	}
	
	public static void main(String[] args){
		P080OverLoad p = new P080OverLoad();
//		p.testConstVal();
		p.testChar();
	}
}

