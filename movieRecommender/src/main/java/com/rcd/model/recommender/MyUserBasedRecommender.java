package com.rcd.model.recommender;
import org.apache.mahout.cf.taste.impl.neighborhood.*;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.*;
import org.apache.mahout.cf.taste.neighborhood.*;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.similarity.*;

import com.rcd.model.MyDataModel;

import java.util.*;

/**
 * 基于用户的推荐
 */
public class MyUserBasedRecommender {
	public List<RecommendedItem> userBasedRecommender(long userID,int size) {
		// step:1 构建模型 2 计算相似度 3 查找k紧邻 4 构造推荐引擎
		//RecommendedItem：
			//encapsulate(封装) items that are recommended, and include the item recommended and a value
		List<RecommendedItem> recommendations = null;
		try {
			DataModel model = MyDataModel.myDataModel();//构造数据模型
			//用PearsonCorrelation 算法计算用户相似度
			//UserSimilarity:
		    	//Implementations of this interface define a notion of similarity between two users. 
				//return values in the range -1.0 to 1.0, with 1.0 representing perfect similarity.
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			
			//计算用户的"邻居"，这里将与该用户最近距离为 3 的用户设置为该用户的"邻居"
			//NearestNUserNeighborhood:
				//Computes a neighborhood consisting of the nearest n users to a given user
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);
			
			//根据model和neighborhood进行推荐，并将结果缓存在内存中
				//采用 CachingRecommender为RecommendationItem进行缓存，
			//Recommender:Implementations of this interface can recommend items for a user
			//CachingRecommender:A Recommender which caches the results from another Recommender in memory
			//GenericUserBasedRecommender:
			    //A simple Recommender which uses a given DataModel and UserNeighborhood to produce recommendations 
			Recommender recommender = new CachingRecommender(
					new GenericUserBasedRecommender(model, neighborhood, similarity));
			
			//得到推荐的结果，size是推荐接过的数目
			recommendations = recommender.recommend(userID, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recommendations;
	}
}