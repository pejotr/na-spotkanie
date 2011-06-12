package eu.doniec.piotr.naspotkanie.web.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import eu.doniec.piotr.naspotkanie.entity.Share;
import eu.doniec.piotr.naspotkanie.entity.User;


public class UserDAO {

	public boolean isUserRegistered(String email) {
		
		EntityManager em = EMFService.get().createEntityManager();
		Query q = em.createQuery("select u from User u where u.email = '" + email + "'");
		List<User> user = q.getResultList();
		
		if( user.size() == 0 ) {
			System.out.println("[INFO] User [#email=" + email + "] is not registered");
			return false;
		}
		
		return true;
	}
	
	public boolean registerUser(String email, String password) {

		synchronized(this) {
			EntityManager em = EMFService.get().createEntityManager();
			
			User newUser = new User(email, password); 
			em.persist(newUser);
			em.close();
		}
		
		return true;
	}
	
	public User getUser(String email) {
		EntityManager em = EMFService.get().createEntityManager();
		
		Query q = em.createQuery("select u from User u where u.email = '" + email + "'");
		User user = (User)q.getSingleResult();
		return user;
	}
	
	public void setUserPosition(double lgt, double lat, long id) {
		EntityManager em = EMFService.get().createEntityManager();
		
		try {
			Query q = em.createQuery("select u from User u where u.id = " + id + "");
			User user = (User)q.getSingleResult();			
			
			user.setLatitude(lat);
			user.setLongitude(lgt);
			
			em.merge(user);
			
		} finally {
			em.close();
		}
	}
	
	public List<User> getAllUsers() {
		EntityManager em = EMFService.get().createEntityManager();

		Query q = em.createQuery("select from User u");
		List<User> users = q.getResultList();
		
		System.out.println("[INFO] There are " + Integer.toString(users.size()) + " users registered");
		return users;
	}
	
	public List<User> getUsersOnList(String[] users) {
		EntityManager em = EMFService.get().createEntityManager();
		StringBuilder userSet = new StringBuilder();
		
		for(String u : users) {
			userSet.append("'").append(u).append("'").append(",");
		}
		
		System.out.println("[DEBUG] UserDAO > " + userSet );
		String userSetQuery = userSet.substring(0, userSet.length() - 1);
		System.out.println("[DEBUG] UserDAO > SQL query " + "select from User u WHERE u.email IN (" + userSetQuery + ")" );
		
		Query q = em.createQuery("SELECT FROM User u WHERE u.email IN (" + userSetQuery + ")");
		List<User> usersList = q.getResultList();
		
		System.out.println("[DEBUG] UserDAO > Cosik :) ");
		return usersList;
	}
	
	public List<User> getUsersOnList(Long ownerId, List<Share> shares) {
		
		if(shares.size() == 0) {
			return new ArrayList<User>();
		}
		
		EntityManager em = EMFService.get().createEntityManager();
		StringBuilder userSet = new StringBuilder();
		
		for(Share s : shares) {
			userSet.append(s.getId1()).append(",");
		}
		
		userSet.append(ownerId);
		
		//System.out.println("[DEBUG] UserDAO::getUsersOnList > " + userSet );
		//String userSetQuery = userSet.substring(0, userSet.length());
		System.out.println("[DEBUG] UserDAO::getUsersOnList > SQL query " + "select from User u WHERE u.id IN (" + userSet + ")" );
		
		Query q = em.createQuery("SELECT FROM User u WHERE u.id IN (" + userSet + ")");
		List<User> usersList = q.getResultList();
		
		System.out.println("[DEBUG] UserDAO::getUsersOnList > Cosik :) ");
		return usersList;
	}
	
}
