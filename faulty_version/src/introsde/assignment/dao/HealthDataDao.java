package introsde.assignment.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import introsde.assignment.model.Measure;
import introsde.assignment.model.MeasureDefinition;
import introsde.assignment.model.Person;

public enum HealthDataDao {
	instance;
	private EntityManagerFactory emf;
	
	private HealthDataDao() {
		if (emf!=null) {
            emf.close();
        }
		emf = Persistence.createEntityManagerFactory("ISDE3");
	}
	
	public EntityManager createEntityManager() {
        try {
            return emf.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;    
}

	public void closeConnections(EntityManager em) {
		em.close();
	}

	public EntityTransaction getTransaction(EntityManager em) {
		return em.getTransaction();
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

	public static List<Person> getAllPeople() {
		EntityManager em = instance.createEntityManager();
	    List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
	    instance.closeConnections(em);
	    return list;
	}
	
	public static Person getPersonById(Long id) {
		EntityManager em = instance.createEntityManager();
		Person p = em.find(Person.class, id);
		instance.closeConnections(em);
	    return p;
	}
	
	public static Person updatePerson(Person p) {
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    instance.closeConnections(em);
	    return p;
	}
	
	public static Person savePerson(Person p) {
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    instance.closeConnections(em);
	    return p;
	}
	
	public static Person removePerson(Long personId) {
		Person p = HealthDataDao.getPersonById(personId);
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.remove(p);
		tx.commit();
	    instance.closeConnections(em);
	    return p;
	}
	
	public static List<Measure> getSpecificPersonMeasures(Long personId, String measureType) {
		Person person = HealthDataDao.getPersonById(personId);
		//get all measures
		List<Measure> allList = person.getMeasures();
		//create output
		List<Measure> filteredList = new ArrayList<>();
		Iterator<Measure> iter = allList.iterator();
		while(iter.hasNext()) {
			Measure m = iter.next();
			//filtering
			if(measureType.equals(m.getMeasureType())) {
				filteredList.add(m);
			}
		}
		return filteredList;
	}
	
	public static Measure getMeasure(Long personId, String measureType, Long mid) {
		Person p = HealthDataDao.getPersonById(personId);
		if(p==null) return null;
		//get all old measure
		List<Measure> allList = p.getMeasures();
		if(allList!=null) {
			Iterator<Measure> iter = allList.iterator();
			while(iter.hasNext()) {
				Measure m = iter.next();
				if(mid.equals(m.getMid()) && measureType.equals(m.getMeasureType())) {
					return m;
				}
			}
		}		
		//not found on old measure, search on current
		List<Measure> currentList = p.getCurrentHealth().getCurrentMeasures();
		if(currentList==null) return null;
		Iterator<Measure> currentIter = currentList.iterator();
		while(currentIter.hasNext()) {
			Measure m = currentIter.next();
			if(mid.equals(m.getMid()) && measureType.equals(m.getMeasureType())) {
				return m;
			}
		}
		//not found
		return null;
	}
	
	public static Measure savePersonMeasure(Long personId, Measure m) {
		 
	    //##### Step 1: update measure definition	
		EntityManager em = instance.createEntityManager();
	    List<MeasureDefinition> mds = em.createNamedQuery("MeasureDefinition.findAll", MeasureDefinition.class).getResultList();
	    instance.closeConnections(em);
	    //no def at the moment, create it
	    if(mds==null) {
	    	MeasureDefinition md = new MeasureDefinition();
	    	md.setMeasureType(m.getMeasureType());
	    	HealthDataDao.insertMeasureDefinition(md);
	    }
	    else {
	    	//find for a definition
	    	ListIterator<MeasureDefinition> iter = mds.listIterator();
	    	boolean found = false;
			while(iter.hasNext() && !found) {
				MeasureDefinition current = iter.next();
				if(current.getMeasureType().equals(m.getMeasureType())) {
					found = true;
				}
			}
			//create def if not found
			if(!found) {
				MeasureDefinition md = new MeasureDefinition();
		    	md.setMeasureType(m.getMeasureType());
		    	HealthDataDao.insertMeasureDefinition(md);
			}
	    }
	    
	    //##### Step 2: save measure in history
	    Person p = HealthDataDao.getPersonById(personId);
	    if(p==null) return null;
	    List<Measure> measures = p.getMeasures();
	    if(measures==null) {
			 p.setMeasures(new ArrayList<Measure>());
		}
	    measures = p.getMeasures();
		measures.add(m);
		HealthDataDao.saveMeasure(m);
		Person p1 = HealthDataDao.updatePerson(p);
		System.out.println(p1.getMeasures().size());
	    return m;
	}
	
	public static Measure saveMeasure(Measure m) {
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(m);
		tx.commit();
	    instance.closeConnections(em);
	    return m;
	}

	private static Measure updateMeasure(Measure m) {
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.merge(m);
		tx.commit();
	    instance.closeConnections(em);
	    return m;
	}
	
	public static Measure updatePersonMeasure(Long personId, Measure m) {
		Measure old = HealthDataDao.getMeasure(personId, m.getMeasureType(), m.getMid());
		if(old==null) return null;
		if(m.getDateRegistered()!=null) {
			old.setDateRegistered(m.getDateRegistered());
		}
		if(m.getMeasureValue()!=null) {
			old.setMeasureValue(m.getMeasureValue());
		}		
		HealthDataDao.updateMeasure(m);
		return null;
	}
	
	public static MeasureDefinition insertMeasureDefinition(MeasureDefinition md) {
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(md);
		tx.commit();
	    instance.closeConnections(em);
	    return md;	
	}
	
	public static List<MeasureDefinition> getMeasureDefinitions() {
		EntityManager em = instance.createEntityManager();
	    List<MeasureDefinition> list = em.createNamedQuery("MeasureDefinition.findAll", MeasureDefinition.class).getResultList();
	    instance.closeConnections(em);
	    return list;		
	}
}
