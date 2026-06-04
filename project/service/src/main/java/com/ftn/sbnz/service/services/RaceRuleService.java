package com.ftn.sbnz.service.services;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.models.Incident;
import com.ftn.sbnz.model.models.RaceControlDecision;

import com.ftn.sbnz.model.models.DriverViolation;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.InputStream;
import java.util.List;

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

    public DriverViolation evaluateViolationTemplate(DriverViolation violation) {
        InputStream template = getClass().getResourceAsStream("/templates/penalty-template.drt");
        InputStream data = getClass().getResourceAsStream("/templates/penalty-data.xls");

        if (template == null) {
            throw new RuntimeException("Template file not found.");
        }

        if (data == null) {
            throw new RuntimeException("Penalty data file not found.");
        }

        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        String drl = converter.compile(data, template, 2, 1);

        System.out.println(drl);

        KieSession kieSession = createKieSessionFromDRL(drl);

        kieSession.insert(violation);

        if (violation.getDriver() != null) {
            kieSession.insert(violation.getDriver());
        }

        kieSession.fireAllRules();
        kieSession.dispose();

        return violation;
    }

    private KieSession createKieSessionFromDRL(String drl) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);

            for (Message message : messages) {
                System.out.println("Error: " + message.getText());
            }

            throw new IllegalStateException("Compilation errors were found.");
        }

        return kieHelper.build().newKieSession();
    }

}