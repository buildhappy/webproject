package cn.itcast.annotation2;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import cn.itcast.domain.Book;

public class BookDao {
	
	@Inject private ComboPooledDataSource ds;  //class
	
	
	@Inject
	public void setDs(ComboPooledDataSource ds) {
		this.ds = ds;
	}

	public ComboPooledDataSource getDs() {
		return ds;
	}

	public void add(Book book){
	
	}
	
}
