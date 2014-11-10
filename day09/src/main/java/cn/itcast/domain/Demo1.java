package cn.itcast.domain;

import java.math.BigDecimal;

public class Demo1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//01010100101010   64
		/*double a = 0.1;
		double b = 0.006;
		
		System.out.println(a+b);*/
		
		
		BigDecimal a = new BigDecimal("0.109328092840430832048209480328403284032840328403284032840238403280");
		BigDecimal b = new BigDecimal("0.006209340932840923840238402384092384092384092384032840328403284092384032840932840328409328409328409328409328403284032840932");
		
		System.out.println(a.add(b).toString());
		System.out.println(a.multiply(b).toString());
		System.out.println(a.divide(b,10,BigDecimal.ROUND_HALF_UP));
		
		

	}

}
