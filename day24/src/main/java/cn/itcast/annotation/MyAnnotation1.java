package cn.itcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target(value={ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@Inherited
public @interface MyAnnotation1 {
	
	String name() default "zxx";
	String password() default "123";
	int age() default 12;
	Gender gender() default Gender.男;
	MyAnnotation2 my2() default @MyAnnotation2(name="llll");
	Class clazz() default String.class;
	
	String[] ss() default {"aa","bbb"};
	int[] i() default {1,2};
	
}
