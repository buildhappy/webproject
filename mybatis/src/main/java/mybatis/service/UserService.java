package mybatis.service;

import java.util.List;

import mybatis.entity.User;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 实际上就是官方文档所述的Mapper
 * @author Administrator
 *
 */
public interface UserService {
	@Insert("insert into users (id,username,password) values (#{id},#{username},#{password})")
	void add(@Param("id")String id , @Param("username")String username,@Param("password")String password);
	
	@Delete("delete from users where id=#{id}")
	void delete(@Param("id")String id);
	
	@Update("update users set username=#{username},password=#{password} where id=#{id}")
	int update(@Param("username")String username,@Param("password")String password,@Param("id")String id);
	
	@Select("select * from users where id=#{id}")
	User getUser(@Param("id")String id);
	
	@Select("select * from users order by id asc")
	List<User> getUsers();
	
	@Select("select * from users order by id asc limit #{pageSize} offset #{offset}")
	List<User> getUsersByPage(@Param("offset")int offset,@Param("pageSize")int pageSize);
							//offset=pageSize*(page-1)
	//使用注解时接口的方法不能重载，否者会产生Mapped Statements collection does not contain value for异常,
	//另外还应尽量避免配置和注解混合使用的情况
}
