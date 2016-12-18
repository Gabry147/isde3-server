package introsde.assignment.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "currentHealth")
public class CurrentHealth {
	
	private List<Measure> currentMeasures;
	
	public List<Measure> getCurrentMeasures() {
		return currentMeasures;
	}

	public void setCurrentMeasures(List<Measure> currentHealth) {
		this.currentMeasures = currentHealth;
	}

}
