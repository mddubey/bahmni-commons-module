package org.bahmni.module.bahmnicommons.web.v1_0.controller;

import org.bahmni.module.bahmnicommons.contract.encounter.data.ConceptData;
import org.bahmni.module.bahmnicommons.contract.encounter.response.EncounterConfigResponse;
import org.bahmni.module.bahmnicommons.contract.patient.response.PatientConfigResponse;
import org.bahmni.module.bahmnicommons.service.BahmniPatientService;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.OrderType;
import org.openmrs.VisitType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.VisitService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/bahmnicore/config")
public class BahmniCommonsConfigController extends BaseRestController {

    @Autowired
    private BahmniPatientService bahmniPatientService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private EncounterService encounterService;
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET, value = "/patient")
    @ResponseBody
    public PatientConfigResponse getPatientConfig() {
        return bahmniPatientService.getConfig();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/bahmniencounter")
    @ResponseBody
    public EncounterConfigResponse getConfig(@RequestParam("callerContext")String callerContext) {
        EncounterConfigResponse encounterConfigResponse = new EncounterConfigResponse();
        List<VisitType> visitTypes = visitService.getAllVisitTypes();
        for (VisitType visitType : visitTypes) {
            if (!visitType.isRetired()) {
                encounterConfigResponse.addVisitType(visitType.getName(), visitType.getUuid());
            }
        }
        List<EncounterType> allEncounterTypes = encounterService.getAllEncounterTypes(false);
        for (EncounterType encounterType : allEncounterTypes) {
            encounterConfigResponse.addEncounterType(encounterType.getName(), encounterType.getUuid());
        }
        Concept conceptSetConcept = conceptService.getConcept(callerContext);
        if (conceptSetConcept != null) {
            List<Concept> conceptsByConceptSet = conceptService.getConceptsByConceptSet(conceptSetConcept);
            for (Concept concept : conceptsByConceptSet) {
                ConceptData conceptData = new ConceptData(concept.getUuid(), concept.getName().getName());
                encounterConfigResponse.addConcept(concept.getName().getName(), conceptData);
            }
        }
        List<OrderType> orderTypes = orderService.getOrderTypes(true);
        for (OrderType orderType : orderTypes) {
            encounterConfigResponse.addOrderType(orderType.getName(), orderType.getUuid());
        }
        return encounterConfigResponse;
    }

}
