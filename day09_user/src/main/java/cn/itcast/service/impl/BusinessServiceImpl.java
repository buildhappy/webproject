package cn.itcast.service.impl;
/**
 * service层，提供web层的所有业务服务
 */
import cn.itcast.dao.UserDao;
import cn.itcast.dao.impl.UserDaoXmlImpl;
import cn.itcast.domain.User;
import cn.itcast.exception.UserExistException;
import cn.itcast.factory.DaoFactory;
import cn.itcast.utils.ServiceUtils;

public class BusinessServiceImpl {
	
	private UserDao dao = new UserDaoXmlImpl();//mvc设计模式,抽象出Dao接口的目的就是为了使bean的持久化操作(写入数据库或xml文件等)与具体的数据库无关
											   //但此处显然与具体的实现(此处存在xml文件中)相互关联了，所以要想取消关联,可以用工厂模式或spring
	
	//private UserDao dao = DaoFactory.getInstance().createDao(UserDao.class);
	
	//对web层提供注册服务
	public void register(User user) throws UserExistException{
		
		//先判断当前要注册的用户是否存在
		boolean b = dao.find(user.getUsername());
		if(b){
			throw new UserExistException();  //发现要注册的用户已存在，则给web层抛一个编译时异常，提醒web层处理这个异常，给用户一个友好提示
		}else{
			user.setPassword(ServiceUtils.md5(user.getPassword()));
			dao.add(user);
		}
	}
	
	//对web层提供登陆服务
	public User login(String username,String password){  //aaa 123
		password = ServiceUtils.md5(password);
		return dao.find(username, password);
	}
}