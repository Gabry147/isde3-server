package introsde.assignment.soap;

import java.util.List;

import javax.jws.WebService;

import introsde.assignment.dao.HealthDataDao;
import introsde.assignment.model.Measure;
import introsde.assignment.model.MeasureDefinition;
import introsde.assignment.model.Person;

@WebService(endpointInterface = "introsde.assignment.soap.People", serviceName="PeopleService")
public class PeopleImplementation implements People{

	//req #01
	@Override
	public List<Person> readPersonList() {
		return HealthDataDao.getAllPeople();
	}

	//req #02
	@Override
	public Person readPerson(Long id) {
		return HealthDataDao.getPersonById(id);
	}

	//req #03
	@Override
	public Person updatePerson(Person person) {
		return HealthDataDao.updatePerson(person);
	}

	//req #04
	@Override
	public Person createPerson(Person person) {
		return HealthDataDao.savePerson(person);
	}

	//req #05
	@Override
	public Person deletePerson(Long id) {
		return HealthDataDao.removePerson(id);
	}

	//req #06
	@Override
	public List<Measure> readMeasureHistory(Long id, String measureType) {
		return HealthDataDao.getSpecificPersonMeasures(id, measureType);
	}

	//req #07
	@Override
	public List<MeasureDefinition> readMeasureTypes() {
		return HealthDataDao.getMeasureDefinitions();
	}

	//req #08
	@Override
	public Measure readPersonMeasure(Long id, String measureType, Long mid) {
		return HealthDataDao.getMeasure(id, measureType, mid);
	}

	//req #09
	@Override
	public Measure savePersonMeasure(Long id, Measure m) {
		return HealthDataDao.savePersonMeasure(id, m);
	}

	//req #10
	@Override
	public Measure updateMeasure(Long id, Measure m) {
		return HealthDataDao.updatePersonMeasure(id, m);
	}

}
