package com.buildhappy.spitter.persistence;

import java.util.List;

import com.buildhappy.spitter.domain.Spitter;
import com.buildhappy.spitter.domain.Spittle;
/**
 * 提供Spitter类，操作数据库的接口
 * @author Administrator
 *
 */
public interface SpitterDao {
  void addSpitter(Spitter spitter);

  void saveSpitter(Spitter spitter);

  Spitter getSpitterById(long id);

  List<Spittle> getRecentSpittle();
  
  void saveSpittle(Spittle spittle);
  
  List<Spittle> getSpittlesForSpitter(Spitter spitter);

  Spitter getSpitterByUsername(String username);
  
  void deleteSpittle(long id);
  
  Spittle getSpittleById(long id);
  
  List<Spitter> findAllSpitters();
}
