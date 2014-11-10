package cn.itcast.annotation2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DbInfo {

	String driver();

	String url();

	String username();

	String password();

}
