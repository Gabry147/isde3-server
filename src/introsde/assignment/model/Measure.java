package introsde.assignment.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="Measure")
@NamedQuery(name="Measure.findAll", query="SELECT m FROM Measure m")
@XmlRootElement(name="measure")
public class Measure implements Comparable<Measure>, Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="mid")
	private Long mid;
	
	@Temporal(TemporalType.DATE)
	@Column(name="dateRegistered")
	private Date dateRegistered;
	
	@Column(name="measureType")
	private String measureType;
	
	@Column(name="measureValue")
	private String measureValue;
	
	@Column(name="measureValueType")
	private String measureValueType;
	
	@Column(name="personId")
	private Long personId;

	public Long getMid() {
		return mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}

	public Date getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public String getMeasureValue() {
		return measureValue;
	}

	public void setMeasureValue(String measureValue) {
		this.measureValue = measureValue;
	}

	public String getMeasureValueType() {
		return measureValueType;
	}

	public void setMeasureValueType(String measureValueType) {
		this.measureValueType = measureValueType;
	}
	
	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	@Override
	public int compareTo(Measure o) {
		if(o.getDateRegistered().before(this.getDateRegistered())) {
			return -1;
		}		
		return 1;
	}
}
