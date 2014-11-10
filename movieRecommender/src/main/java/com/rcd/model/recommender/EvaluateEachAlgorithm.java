package com.rcd.model.recommender;

/**
 * 对各种算法的评价
 * @author Administrator
 *
 */
public class EvaluateEachAlgorithm {
//	Mahout有很多推荐的实现，下面对各自特点进行介绍：
//	1、GenericUserBasedRecommender：
//	基于用户的推荐，用户数量相对较少时速度较快。 
	
//	2、GenericItemBasedRecommender：
//	基于物品的推荐，物品数量较少时速度较快，外部提供了物品相似度数据后会更加有效率。 
	
//	3、SlopeOneRecommender：
//	基于slope-one算法（想想那个填空的表格吧）的推荐，在线推荐或更新比较快，需要先下大量的预处理运算。物品数量相对较少时使用比较合适。 

//	4、SVDRecommender：
//	效果不错，和slope-one一样，事先需要大量的预处理运算。 
	
//	5、KnnItemBasedRecommender：
//	基于最近邻算法的推荐器，物品数量较小时表现良好。
	
//	6、TreeClusteringRecommender：
//	基于聚类的推荐器，在线推荐较快，同时也需要事先大量预处理运算，用户数量相对较少时表现良好。
}
