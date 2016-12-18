package introsde.assignment.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import introsde.assignment.dao.HealthDataDao;

@Entity
@Table(name="Person") 
@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p")
@XmlRootElement(name="person")
public class Person implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="personId")
	private Long personId;
	
	@Column(name="firstname")
	private String firstname;

	@Column(name="lastname")
	private String lastname;
	
	@Temporal(TemporalType.DATE)
	@Column(name="birthdate")
	private Date birthdate;
	
	@Transient
	private CurrentHealth currentHealth;

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public CurrentHealth getCurrentHealth() {
		CurrentHealth ch = new CurrentHealth();
		List<Measure> currentMeasures = new ArrayList<Measure>();
		List<MeasureDefinition> mds = HealthDataDao.getMeasureDefinitions();
		Map<String, Boolean> measureTypes = new HashMap<>();
		Iterator<MeasureDefinition> iter = mds.iterator();
		while(iter.hasNext()){
			MeasureDefinition md = iter.next();
			measureTypes.put(md.getMeasureType(), false);
		}
		List<Measure> measures = HealthDataDao.getPersonMeasures(this.personId);
		Collections.sort(measures);
		Iterator<Measure> iterator = measures.iterator();
		while(iterator.hasNext()){
			Measure m = iterator.next();
			if(! measureTypes.get(m.getMeasureType())) {
				currentMeasures.add(m);
				measureTypes.put(m.getMeasureType(), true);				
			}
		}
		ch.setCurrentMeasures(currentMeasures);
		return ch;
	}

	public void setCurrentHealth(CurrentHealth currentHealth) {
		this.currentHealth = currentHealth;
	}
}
