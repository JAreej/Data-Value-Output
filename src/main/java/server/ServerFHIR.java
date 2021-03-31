package server;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import error.DuplicateServerException;
import model.ObservationModel;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class ServerFHIR {

    private static final String BASE_URL = "http://localhost:8080";
    private static int instances = 0;
    FhirContext ctx = FhirContext.forR4();
    IGenericClient client = ctx.newRestfulGenericClient(BASE_URL);

    public ServerFHIR() {
        System.out.println("Constructing Server");
        instances++;
        if (instances > 1) {
            throw new DuplicateServerException("More than one instance of server created! Please reuse previous instance!");
        }
    }

    //todo
   /* public boolean testConnection() {
        System.out.println("TestConnection");
        HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/metadata").asJson();
        int status = response.getStatus();

        System.out.println(response.getStatusText());
        System.out.println(response.isSuccess());
        System.out.println(response.getStatus());
        if (status == 200) {
            return true;
        }
        return false;
    }*/

    //todo
    //String firstname, String lastname, Enumerations.AdministrativeGender gender
    public String createPatient() {
        Patient newPatient = new Patient();
        newPatient
                .addName()
                .setFamily("Doe")
                .addGiven("John");
        newPatient.setGender(Enumerations.AdministrativeGender.MALE);

        MethodOutcome patientOutcome = client
                .create()
                .resource(newPatient)
                .execute();

        ArrayList<String> allPatients = getPatients();
        String patientID = allPatients.get(allPatients.size() - 1);
        return patientID;
    }

    //todo
    //Observation observation, String patientID
    public String createNumericalObservation() {
        Observation observation = new Observation();

        observation.getCode().addCoding().setSystem("http://sfb125.de/ontology/ihCCApplicationOntology/").setCode("bilirubin_concentration");
        observation.getSubject().setReference("Patient/da4bdecf-7341-42ca-81d7-912de37d1399");
        observation.getValueQuantity().setValue(1).setUnit("mg/dl");
        MethodOutcome observationOutcome = client
                .create()
                .resource(observation)
                .execute();
        String observationId = observationOutcome.getId().getIdPart();
        System.out.println("Created numerical observation, got ID: " + observationId);
        return null;
    }

    //todo
    //Observation observation, String patientID
    public String createCategoricalObservation() {
        Observation observation = new Observation();

        observation.getCode().addCoding().setSystem("http://sfb125.de/ontology/ihCCApplicationOntology/").setCode("bilirubin_concentration");
        observation.getSubject().setReference("Patient/da4bdecf-7341-42ca-81d7-912de37d1399");
        observation.getValueCodeableConcept().addCoding().setSystem("http://sfb125.de/ontology/ihCCApplicationOntology/").setCode("cN1");
        MethodOutcome observationOutcome = client
                .create()
                .resource(observation)
                .execute();
        String observationId = observationOutcome.getId().getIdPart();
        System.out.println("Created categorical observation, got ID: " + observationId);
        return null;
    }


    public ArrayList<String> getPatients() {
        org.hl7.fhir.r4.model.Bundle results = client
                .search()
                .forResource(Patient.class)
                .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
                .execute();

        ArrayList<String> allPatientIDs = new ArrayList<>();
        List<Bundle.BundleEntryComponent> entries = results.getEntry();
        for (int i = 0; i < entries.size(); i++) {
            String patientID = results.getEntry().get(i).getResource().getId().split("http://localhost:8080/Patient/")[1];
            System.out.println(patientID);
        }
        System.out.println("All patients: ");
        System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(results));

        System.out.println("AllpatientIDs " + allPatientIDs);
        return allPatientIDs;
    }

    //todo
    //String patientID
    public ArrayList<ObservationModel> getObservationsOfPatient() {
        String patientID = "Patient/da4bdecf-7341-42ca-81d7-912de37d1399";
        org.hl7.fhir.r4.model.Bundle results = client
                .search()
                .forResource(Observation.class)
                .where(Observation.SUBJECT.hasId(patientID))
                .returnBundle(org.hl7.fhir.r4.model.Bundle.class)
                .execute();

        System.out.println("All observations: ");
        System.out.println(ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(results));

        ArrayList<ObservationModel> allObservations = new ArrayList<>();
        List<Bundle.BundleEntryComponent> entries = results.getEntry();


        for (int i = 0; i < entries.size(); i++) {
            String observationID = entries.get(i).getResource().getId();
            Observation observationResource = (Observation) entries.get(i).getResource();
            String observationSystem = observationResource.getCode().getCoding().get(0).getSystem();
            String observationCode = observationResource.getCode().getCoding().get(0).getCode();
            if (observationResource.hasValueQuantity()) {
                Double observationValue = observationResource.getValueQuantity().getValue().doubleValue();
                String observationUnit = observationResource.getValueQuantity().getUnit();
                ObservationModel observation = new ObservationModel(observationSystem, observationCode, observationValue, observationUnit);
                observation.setObservationID(observationID);
                observation.setPatientID(patientID);
                allObservations.add(observation);
            } else if (observationResource.hasValueCodeableConcept()) {
                String valueSystem = observationResource.getValueCodeableConcept().getCoding().get(0).getSystem();
                String valueCode = observationResource.getValueCodeableConcept().getCoding().get(0).getCode();
                ObservationModel observation = new ObservationModel(observationSystem, observationCode, valueSystem, valueCode);
                observation.setObservationID(observationID);
                observation.setPatientID(patientID);
                allObservations.add(observation);
            }
        }
        return allObservations;
    }
}
