package com.buildhappy.spitter.service;
/**
 * 提供所有对Spitter的操作，包括通过调用Dao层对数据库的操作
 * 如：查看所有Spitter的Spittle，保存Spitter的Spittle等
 */
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;//stereotype固定的形式
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.buildhappy.spitter.domain.Spitter;
import com.buildhappy.spitter.domain.Spittle;
import com.buildhappy.spitter.persistence.JdbcSpitterDao;
import com.buildhappy.spitter.persistence.SpitterDao;

@Service("spitterService")
//定义一个事务；表示该方法必须运行在事务中，否则启动一个新的事务
@Transactional(propagation=Propagation.REQUIRED)
public class SpitterServiceImpl implements SpitterService{
	
	// @Autowired
	SpitterDao spitterDao = new JdbcSpitterDao();
	
	public void saveSpittle(Spittle spittle){
		spittle.setWhen(new Date());
		spitterDao.saveSpittle(spittle);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS , readOnly=true)
					//该方法不需要事务上下文，有事务也不会在事务中运行;
	public List<Spittle> getRecentSpittles(int count){
		List<Spittle> recentSpittles = spitterDao.getRecentSpittle();
		Collections.reverse(recentSpittles);
		return recentSpittles.subList(0, Math.min(49 , recentSpittles.size()));
	}
	
	public void saveSpitter(Spitter spitter){
		if(spitter.getId() == 0.0){    ///?????????????
			spitterDao.addSpitter(spitter);
		}else{
			spitterDao.saveSpitter(spitter);
		}
	}
	
	@Transactional(propagation=Propagation.SUPPORTS , readOnly=true)
	public Spitter getSpitter(long id){
		return spitterDao.getSpitterById(id);
	}
	
	public void startFollowing(Spitter followers , Spitter follows){
	}
	
	/**
	 * 为spitter取出相应的Spittle
	 */
	public List<Spittle> getSpittlesForSpitter(Spitter spitter){
		return spitterDao.getSpittlesForSpitter(spitter);
	}
	public List<Spittle> getSpittlesForSpitter(String username) {
		Spitter spitter = spitterDao.getSpitterByUsername(username);
		return getSpittlesForSpitter(spitter);
	}
	
	public Spitter getSpitter(String username){
		return spitterDao.getSpitterByUsername(username);
	}

	public void deleteSpittle(long id) {
		spitterDao.deleteSpittle(id);
	}
	public List<Spitter> getAllSpitters() {
		return spitterDao.findAllSpitters();
	}
	
	public Spittle getSpittleById(long id) {
		return spitterDao.getSpittleById(id);
	}
	
}
