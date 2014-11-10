package ch09interface;
/**
 * 使用适配器模式，被适配的类可以通过继承和实现Readable接口来创建。
 */
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Random;
import java.util.Scanner;

public class P183AdaptedRandomDoubles extends RandomDoubles implements Readable{
	private int count;
	public P183AdaptedRandomDoubles(int c){
		this.count = c;
	}
	public int read(CharBuffer cb){
		if(count-- == 0){
			return -1;
		}
		String result  = Double.toString(next());
		cb.append(result);
		//System.out.println(result);
		return result.length();
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(new P183AdaptedRandomDoubles(7));
		while(s.hasNext()){
			System.out.print(s.next() + "  ");
		}
		s.close();
	}

}

class RandomDoubles{
	private static Random rand = new Random(47);
	public double next(){
		return rand.nextDouble();
	}
}
