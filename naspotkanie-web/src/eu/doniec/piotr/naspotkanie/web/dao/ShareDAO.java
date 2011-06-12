package eu.doniec.piotr.naspotkanie.web.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import eu.doniec.piotr.naspotkanie.entity.Share;
import eu.doniec.piotr.naspotkanie.entity.User;

public class ShareDAO {

	public List<Share> getUserShares(Long userId) {
		EntityManager em = EMFService.get().createEntityManager();

		Query q = em.createQuery("select from Share s WHERE s.id1 = " + userId + "");
		List<Share> shares = q.getResultList();
		
		System.out.println("[INFO] ShareDAO > User " + userId + " has " + shares.size()  + " sharings");
		return shares;
	}

	/*
	 * Get users IDs that provide location data to user specified by userId parameter
	 */
	public List<Share> getUserFriendShares(Long userId) {
		EntityManager em = EMFService.get().createEntityManager();

		Query q = em.createQuery("select from Share s WHERE s.id2 = " + userId + "");
		List<Share> shares = q.getResultList();
		
		System.out.println("[INFO] ShareDAO > User " + userId + " has " + shares.size()  + " sharings");
		return shares;
	}
	
	public List<Share> getAllShares() {
		EntityManager em = EMFService.get().createEntityManager();

		Query q = em.createQuery("SELECT FROM Share s");
		List<Share> shares = q.getResultList();
		
		return shares;		
	}
	
	public boolean addShares(User owner, List<User> users) {
		
		if( users.isEmpty() ) {
			System.out.println("[DEBUG] ShareDAO > Users list is empty");
			return false;
		}
		
		synchronized (this) {
			
			for( User u : users ) {
				
				if(owner.getId() != u.getId()) {
					EntityManager em = EMFService.get().createEntityManager();
					Share s1 = new Share();
					s1.setId1(owner.getId());
					s1.setId2(u.getId());				
					em.persist(s1);
					em.close();
					
					em = EMFService.get().createEntityManager();
					Share s2 = new Share();
					s2.setId1(u.getId());
					s2.setId2(owner.getId() );
					em.persist(s2);
					em.close();
				}
			}

		}
		
		System.out.println("[DEBUG] ShareDAO > Shares inserted, I hope");		
		return true;
	}
	
}
