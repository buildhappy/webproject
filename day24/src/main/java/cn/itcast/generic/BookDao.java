package cn.itcast.generic;
/**
 * BookDao继承BaseDao
 */
import cn.itcast.domain.Book;

public class BookDao extends BaseDao<Book> {
	//要复写构造函数
	public BookDao(){
//		super(Book.class);
	}
}
