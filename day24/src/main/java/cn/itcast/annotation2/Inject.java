package cn.itcast.annotation2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

	String driverClass() default "com.mysql.jdbc.Driver";

	String jdbcUrl() default "jdbc:mysql://localhost:3306/bookstore";

	String user() default "root";

	String password() default "root";

}
