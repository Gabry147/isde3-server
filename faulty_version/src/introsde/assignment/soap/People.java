
package introsde.assignment.soap;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import introsde.assignment.model.Measure;
import introsde.assignment.model.MeasureDefinition;
import introsde.assignment.model.Person;

@WebService(name = "People", targetNamespace = "http://soap.assignment.introsde/")
public interface People {

	@WebMethod
    @WebResult(name = "people")
	public List<Person> readPersonList();

	@WebMethod
	@WebResult(name = "person")
	public Person readPerson(@WebParam(name="personId") Long id);

	@WebMethod
	@WebResult(name = "person")
	public Person updatePerson(@WebParam(name="person") Person person);

	@WebMethod
	@WebResult(name = "person")
	public Person createPerson(@WebParam(name="person") Person person);

	@WebMethod
	@WebResult(name = "person")
	public Person deletePerson(@WebParam(name="personId") Long id);

	@WebMethod
	@WebResult(name = "healthProfile-history")
	public List<Measure> readMeasureHistory(@WebParam(name="personId") Long id, @WebParam(name="measureType") String measureType);

	@WebMethod
	@WebResult(name = "measureDefinition")
	public List<MeasureDefinition> readMeasureTypes();

	@WebMethod
	@WebResult(name = "measure")
	public Measure readPersonMeasure(@WebParam(name="personId") Long id, @WebParam(name="measureType") String measureType, @WebParam(name="measureId") Long mid);

	@WebMethod
	@WebResult(name = "measure")
	public Measure savePersonMeasure(@WebParam(name="personId") Long id, @WebParam(name="measure") Measure m);

	@WebMethod
	@WebResult(name = "measure")
	public Measure updateMeasure(@WebParam(name="personId") Long id, @WebParam(name="measure") Measure m);
}
