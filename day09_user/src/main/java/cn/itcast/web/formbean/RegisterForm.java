package cn.itcast.web.formbean;
/**
 * 对register.jsp提交的form表单的信息进行校验
 */
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.MediaSize.ISO;

import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

public class RegisterForm {
	
	private String username;
	private String password;
	private String password2;
	private String email;
	private String birthday;
	private String nickname;
	private String client_checkcode;
	private String server_checkcode;
	private Map errors = new HashMap();//存放校验错误后的错误
	
	public String getClient_checkcode() {
		return client_checkcode;
	}
	public void setClient_checkcode(String client_checkcode) {
		this.client_checkcode = client_checkcode;
	}
	public String getServer_checkcode() {
		return server_checkcode;
	}
	public void setServer_checkcode(String server_checkcode) {
		this.server_checkcode = server_checkcode;
	}
	public Map getErrors() {
		return errors;
	}
	public void setErrors(Map errors) {
		this.errors = errors;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	//用户名不能为空，并且要是3-8位字母
	//密码不能为空，并且是3-8位数字
	//确认密码不能为空，并且要和一次一致
	//电子邮箱不能为空，并且要是一个格式合法的邮箱
	//生日可以为空，不为空时，必须要是一个日期
	//昵称不可以为空，并且要是汉字
	public boolean validate(){
		boolean isOK = true;
		
		if(this.username==null || this.username.trim().equals("")){
			isOK = false;
			errors.put("username", "用户名不能为空！！");
		}else{
			if(!this.username.matches("[A-Za-z]{3,8}")){
				isOK = false;
				errors.put("username", "用户名必须要是3-8位字母！！");
			}
		}
				
		if(this.password==null || this.password.trim().equals("")){
			isOK = false;
			errors.put("password", "密码不能为空！！");
		}else{
			if(!this.password.matches("\\d{3,8}")){
				isOK = false;
				errors.put("password", "密码必须要是3-8位数字！！");
			}
		}
		
		if(this.password2==null || this.password2.trim().equals("")){
			isOK = false;
			errors.put("password2", "确认密码不能为空！！");
		}else{
			if(!this.password.equals(this.password2)){
				isOK = false;
				errors.put("password2", "两次密码要一致！！");
			}
		}
				
		//电子邮箱不能为空，并且要是一个格式合法的邮箱
		if(this.email==null || this.email.trim().equals("")){
			isOK = false;
			errors.put("email", "邮箱不能为空！！");
		}else{
			// aaa@sina.com  aaa@sina.com.cn   aa_bb.cc@sina.com.cn
			//  \\w+@\\w+(\\.\\w+)+
			if(!this.email.matches("\\w+@\\w+(\\.\\w+)+")){
				isOK = false;
				errors.put("email", "邮箱格式不对！！！");
			}
		}
		
		////生日可以为空，不为空时，必须要是一个日期
		if(this.birthday!=null && !this.birthday.trim().equals("")){
			try{
				DateLocaleConverter dlc = new DateLocaleConverter();
				dlc.convert(this.birthday, "yyyy-MM-dd");//如果日期格式或超出日期范围,则抛出异常
			}catch (Exception e) {
				isOK = false;
				errors.put("birthday", "日期格式不对！！！");
			}
		}
		
		//昵称不可以为空，并且要是汉字
		if(this.nickname==null || this.nickname.trim().equals("")){
			isOK = false;
			errors.put("nickname", "昵称不能为空！！");
		}else{  //[^\u4e00-\u9fa5]*
			if(!this.nickname.matches("^([\u4e00-\u9fa5]+)$")){
				isOK = false;
				errors.put("nickname", "呢称必须是汉字！！！");
			}
		}
		
		if(client_checkcode==null || this.client_checkcode.trim().equals("")){
			isOK = false;
			errors.put("client_checkcode", "必须要输入认证码！！");
		}else{
			if(!this.client_checkcode.equals(this.server_checkcode)){
				isOK = false;
				errors.put("client_checkcode", "认证码错误！！！");
			}
		}
		return isOK;
	}	
}
