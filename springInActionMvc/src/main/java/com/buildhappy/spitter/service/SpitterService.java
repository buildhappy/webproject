package com.buildhappy.spitter.service;

import java.util.List;

import com.buildhappy.spitter.domain.Spitter;
import com.buildhappy.spitter.domain.Spittle;

/**
 * 对Spitter类操作的接口,定义在Dao层之上
 * @author Administrator
 *
 */
public interface SpitterService {
  List<Spittle> getRecentSpittles(int count);
  void saveSpittle(Spittle spittle);
  
  void saveSpitter(Spitter spitter);
  Spitter getSpitter(long id);
  void startFollowing(Spitter follower, Spitter followee);

  List<Spittle> getSpittlesForSpitter(Spitter spitter);
  List<Spittle> getSpittlesForSpitter(String username);
  Spitter getSpitter(String username);
  
  Spittle getSpittleById(long id);
  void deleteSpittle(long id);
  
  List<Spitter> getAllSpitters();
}
