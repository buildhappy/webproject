package buildhappy.tools.berkeleyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * 使用抽象类封装对Berkeley DB的操作
 * @author Administrator
 *
 */
public abstract class AbstractFrontier implements Serializable {
	private Environment env;
	private static final String CLASS_CATALOG = "java_class_catalog";
	protected StoredClassCatalog javaCatalog;//用于数据库对象压缩存储的Java序列化目录。
	protected Database catalogDatabase;
	protected Database database;
	
	public AbstractFrontier(String homeDirectory)throws DatabaseException , FileNotFoundException{
		//打开env
		System.out.println("Opening environment in:" + homeDirectory);
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		
		env = new Environment(new File(homeDirectory) , envConfig);
		List myDbNames = env.getDatabaseNames();
		for(int i = 0; i < myDbNames.size(); i++){
			System.out.println("数据库" + i + "是：" + myDbNames.toArray()[i]);
		}
		//设置DatabaseConfig
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);
		//打开数据库
		catalogDatabase = env.openDatabase(null , CLASS_CATALOG , dbConfig);
		javaCatalog = new StoredClassCatalog(catalogDatabase);
		//设置DatabaseConfig
		DatabaseConfig dbConfig0 = new DatabaseConfig();
		dbConfig0.setTransactional(true);
		dbConfig0.setAllowCreate(true);
		//打开
		database = env.openDatabase(null, "URL", dbConfig);
	}
	
	//关闭数据库，关闭环境
	public void close() throws DatabaseException{
		database.close();
		javaCatalog.close();
		env.close();
	}
	
	//put方法
	protected abstract void put(Object key , Object value);
	//get方法
	protected abstract Object get(Object key);
	//delete方法
	protected abstract Object delete(Object key);
}
