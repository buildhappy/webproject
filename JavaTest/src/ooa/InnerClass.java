package ooa;


class Outer{
	private int a = 5;
	public void show1(final int x){
		//final int x = 5;
		class Inner{
			public void function(int z){
				System.out.println("In Inner class x = " + x);
			}
		}
		new Inner().function(3);
	}
}

public class InnerClass {
	public static void main(String[] args){
		int x = 6;
		Outer outer = new Outer();
		outer.show1(4);
	}
}
