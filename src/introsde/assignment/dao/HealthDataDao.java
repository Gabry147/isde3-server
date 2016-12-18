package introsde.assignment.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

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

	//return all Person instances
	public static List<Person> getAllPeople() {
		EntityManager em = instance.createEntityManager();
	    List<Person> list = em.createNamedQuery("Person.findAll", Person.class).getResultList();
	    instance.closeConnections(em);
	    return list;
	}
	
	//query the db to get a single Person
	public static Person getPersonById(Long id) {
		EntityManager em = instance.createEntityManager();
		Query query = em.createQuery("SELECT e FROM Person e WHERE e.personId = "+id);
	    Person p = null;
	    try {
	    	p = (Person)query.getSingleResult();
	    } catch (Exception e) {
	    	return null;
	    }
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
	
	//remove person and related measures
	public static Person removePerson(Long personId) {
		Person p = HealthDataDao.getPersonById(personId);
		if(p==null) return null;
		EntityManager em = instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.merge(p);
		em.createQuery("DELETE FROM Person p WHERE p.personId = :id")
        	.setParameter("id", personId)
        	.executeUpdate();
		//delete also related measures
		em.createQuery("DELETE FROM Measure m WHERE m.personId = :id")
    	.setParameter("id", personId)
    	.executeUpdate();
		//em.remove(p);
		tx.commit();
	    instance.closeConnections(em);
	    return p;
	}
	
	//get all measures of a person
	public static List<Measure> getPersonMeasures(Long id) {
		EntityManager em = instance.createEntityManager();
	    List<Measure> list = em.createNamedQuery("Measure.findAll", Measure.class).getResultList();
	    instance.closeConnections(em);
	    List<Measure> personList = new ArrayList<>();
	    Iterator<Measure> iter = list.iterator();
	    while(iter.hasNext()){
	    	Measure m = iter.next();
	    	if(m.getPersonId().equals(id)) {
	    		personList.add(m);
	    	}
	    }
	    return personList;
	}
	
	public static List<Measure> getSpecificPersonMeasures(Long personId, String measureType) {
		Person p = HealthDataDao.getPersonById(personId);
		if(p==null) return null;
		//get all measures
		List<Measure> allList = HealthDataDao.getPersonMeasures(personId);
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
	
	//get a measure given a person id, a type and the id of the measure
	public static Measure getMeasure(Long personId, String measureType, Long mid) {
		Person p = HealthDataDao.getPersonById(personId);
		if(p==null) return null;
		//get all old measure
		List<Measure> allList = HealthDataDao.getPersonMeasures(personId);
		if(allList!=null) {
			Iterator<Measure> iter = allList.iterator();
			while(iter.hasNext()) {
				Measure m = iter.next();
				if(mid.equals(m.getMid()) && measureType.equals(m.getMeasureType())) {
					return m;
				}
			}
		}		
		return null;
	}
	
	//save a measure in the db
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
		m.setPersonId(personId);
		HealthDataDao.saveMeasure(m);
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
		return m;
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
