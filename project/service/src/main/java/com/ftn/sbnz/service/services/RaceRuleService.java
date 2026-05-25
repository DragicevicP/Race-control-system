package com.ftn.sbnz.service.services;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.models.Incident;
import com.ftn.sbnz.model.models.RaceControlDecision;

@Service
public class RaceRuleService {

    private final KieContainer kieContainer;

    public RaceRuleService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public RaceControlDecision evaluateIncident(Incident incident) {
        RaceControlDecision decision = new RaceControlDecision();
        KieSession kieSession = kieContainer.newKieSession();

        kieSession.insert(incident);
        if (incident.getSector() != null) {
            kieSession.insert(incident.getSector());
        }
        kieSession.insert(decision);

        kieSession.fireAllRules();
        kieSession.dispose();

        return decision;
    }
}