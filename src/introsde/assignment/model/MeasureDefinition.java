package introsde.assignment.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="MeasureDefinition")
@NamedQuery(name="MeasureDefinition.findAll", query="SELECT md FROM MeasureDefinition md")
@XmlRootElement(name="measureDefinition")
public class MeasureDefinition implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="mdid")
	private Long mdid;
	
	@Column(name="measureType")
	private String measureType;

	public Long getMdid() {
		return mdid;
	}

	public void setMdid(Long mdid) {
		this.mdid = mdid;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

}
