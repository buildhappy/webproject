package com.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
	public static final SessionFactory factory ;
	static {
		try {
			Configuration con = new Configuration().configure();
			factory = con.buildSessionFactory();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Initial SessionFactory create failed"+e);
			throw new ExceptionInInitializerError(e);
		}
	}
	public static final ThreadLocal<Session> session =new ThreadLocal<Session>();
	public static Session currentSession(){
		Session s =session.get() ;
		if(s==null){
			s=factory.openSession() ;
			session.set(s);
		}
		return s ;
	}
	public static void closeSession(){
		Session s= session.get() ;
		if(s!=null){
			s.close();
		}
		session.set(null);
	}
}
