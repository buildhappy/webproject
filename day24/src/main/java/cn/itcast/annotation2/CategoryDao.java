package cn.itcast.annotation2;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class CategoryDao {
	
	private ComboPooledDataSource combods;

	@Inject(driverClass="com.mysql.jdbc.Driver",jdbcUrl="jdbc:mysql://localhost:3306/bookstore",user="root",password="root")
	public void setCombods(ComboPooledDataSource combods) {
		this.combods = combods;
	}
	
}
