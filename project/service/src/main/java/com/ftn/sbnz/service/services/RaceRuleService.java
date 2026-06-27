package com.ftn.sbnz.service.services;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.models.BlueFlagMonitoring;
import com.ftn.sbnz.model.models.Driver;
import com.ftn.sbnz.model.models.DriverBehaviorReport;
import com.ftn.sbnz.model.models.DriverViolation;
import com.ftn.sbnz.model.models.Incident;
import com.ftn.sbnz.model.models.RaceControlDecision;
import com.ftn.sbnz.model.models.RaceStatus;
import com.ftn.sbnz.model.models.TrackSector;
import com.ftn.sbnz.model.models.VscDeltaMonitoring;

@Service
public class RaceRuleService {

    private final KieContainer kieContainer;

    public RaceRuleService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public RaceControlDecision evaluateIncident(Incident incident) {
        RaceControlDecision decision = new RaceControlDecision();
        decision.setCreatedAt(LocalDateTime.now());
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

    public RaceControlDecision evaluateRaceStatus(RaceStatus raceStatus) {
        RaceControlDecision decision = new RaceControlDecision();
        decision.setCreatedAt(resolveSimulationTime(raceStatus));

        KieSession kieSession = kieContainer.newKieSession();
        insertRaceStatusFacts(kieSession, raceStatus);
        kieSession.insert(decision);

        kieSession.fireAllRules();
        kieSession.dispose();

        return decision;
    }

    public List<DriverViolation> evaluateDriverBehavior(DriverBehaviorReport report,
            List<DriverViolation> previousViolations) {
        if (report == null || report.getDriver() == null) {
            return Collections.emptyList();
        }

        Driver driver = report.getDriver();
        ensureDriverViolationList(driver);
        List<DriverViolation> before = new ArrayList<>(safeViolations(driver));

        KieSession kieSession = kieContainer.newKieSession();
        Set<Object> insertedFacts = Collections.newSetFromMap(new IdentityHashMap<>());
        insertFact(kieSession, insertedFacts, report);
        insertFact(kieSession, insertedFacts, driver);
        for (DriverViolation previousViolation : safeList(previousViolations)) {
            insertFact(kieSession, insertedFacts, previousViolation);
        }
        for (DriverViolation driverViolation : before) {
            insertFact(kieSession, insertedFacts, driverViolation);
        }

        kieSession.fireAllRules();
        kieSession.dispose();

        List<DriverViolation> createdViolations = new ArrayList<>();
        for (DriverViolation violation : safeViolations(driver)) {
            if (!before.contains(violation)) {
                createdViolations.add(evaluateViolationTemplate(violation));
            }
        }

        return createdViolations;
    }

    public RaceStatus evaluateCep(RaceStatus raceStatus) {
        if (raceStatus == null) {
            return null;
        }

        KieSession kieSession = kieContainer.newKieSession();
        insertRaceStatusFacts(kieSession, raceStatus);
        kieSession.fireAllRules();
        kieSession.dispose();

        for (Driver driver : safeList(raceStatus.getDrivers())) {
            ensureDriverViolationList(driver);
            for (DriverViolation violation : safeViolations(driver)) {
                if (violation.getPenalty() == null) {
                    evaluateViolationTemplate(violation);
                }
            }
        }

        return raceStatus;
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

    private void insertRaceStatusFacts(KieSession kieSession, RaceStatus raceStatus) {
        if (raceStatus == null) {
            return;
        }

        Set<Object> insertedFacts = Collections.newSetFromMap(new IdentityHashMap<>());
        insertFact(kieSession, insertedFacts, raceStatus);

        for (TrackSector sector : safeList(raceStatus.getSectors())) {
            insertFact(kieSession, insertedFacts, sector);
        }

        for (Incident incident : safeList(raceStatus.getIncidents())) {
            insertFact(kieSession, insertedFacts, incident);
            if (incident.getSector() != null) {
                insertFact(kieSession, insertedFacts, incident.getSector());
            }
        }

        for (Driver driver : safeList(raceStatus.getDrivers())) {
            ensureDriverViolationList(driver);
            insertFact(kieSession, insertedFacts, driver);
            for (DriverViolation violation : safeViolations(driver)) {
                insertFact(kieSession, insertedFacts, violation);
            }
        }

        for (BlueFlagMonitoring monitoring : safeList(raceStatus.getBlueFlagMonitorings())) {
            insertFact(kieSession, insertedFacts, monitoring);
            if (monitoring.getDriver() != null) {
                ensureDriverViolationList(monitoring.getDriver());
                insertFact(kieSession, insertedFacts, monitoring.getDriver());
            }
        }

        for (VscDeltaMonitoring monitoring : safeList(raceStatus.getVscDeltaMonitorings())) {
            insertFact(kieSession, insertedFacts, monitoring);
            if (monitoring.getDriver() != null) {
                ensureDriverViolationList(monitoring.getDriver());
                insertFact(kieSession, insertedFacts, monitoring.getDriver());
            }
        }
    }

    private void insertFact(KieSession kieSession, Set<Object> insertedFacts, Object fact) {
        if (fact != null && insertedFacts.add(fact)) {
            kieSession.insert(fact);
        }
    }

    private LocalDateTime resolveSimulationTime(RaceStatus raceStatus) {
        if (raceStatus != null && raceStatus.getSimulationTime() != null) {
            return raceStatus.getSimulationTime();
        }
        return LocalDateTime.now();
    }

    private List<DriverViolation> safeViolations(Driver driver) {
        if (driver == null || driver.getViolations() == null) {
            return Collections.emptyList();
        }
        return driver.getViolations();
    }

    private void ensureDriverViolationList(Driver driver) {
        if (driver != null && driver.getViolations() == null) {
            driver.setViolations(new ArrayList<>());
        }
    }

    private <T> List<T> safeList(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().filter(Objects::nonNull).toList();
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
