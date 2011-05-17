package eu.doniec.piotr.naspotkanie.web.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
	
}
