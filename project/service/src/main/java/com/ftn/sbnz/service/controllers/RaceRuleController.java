package com.ftn.sbnz.service.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.models.DriverBehaviorReport;
import com.ftn.sbnz.model.models.DriverViolation;
import com.ftn.sbnz.model.models.Incident;
import com.ftn.sbnz.model.models.RaceControlDecision;
import com.ftn.sbnz.model.models.RaceStatus;
import com.ftn.sbnz.service.dto.DriverBehaviorEvaluationRequest;
import com.ftn.sbnz.service.services.RaceRuleService;

@RestController
@RequestMapping("/api/rules")
public class RaceRuleController {

    private final RaceRuleService raceRuleService;

    public RaceRuleController(RaceRuleService raceRuleService) {
        this.raceRuleService = raceRuleService;
    }

    @PostMapping("/incident")
    public RaceControlDecision evaluateIncident(@RequestBody Incident incident) {
        return raceRuleService.evaluateIncident(incident);
    }

    @PostMapping("/race-status")
    public RaceControlDecision evaluateRaceStatus(@RequestBody RaceStatus raceStatus) {
        return raceRuleService.evaluateRaceStatus(raceStatus);
    }

    @PostMapping("/driver-behavior")
    public List<DriverViolation> evaluateDriverBehavior(@RequestBody DriverBehaviorEvaluationRequest request) {
        if (request == null) {
            return List.of();
        }
        return raceRuleService.evaluateDriverBehavior(request.getReport(), request.getPreviousViolations());
    }

    @PostMapping("/driver-behavior/report")
    public List<DriverViolation> evaluateDriverBehaviorReport(@RequestBody DriverBehaviorReport report) {
        return raceRuleService.evaluateDriverBehavior(report, List.of());
    }

    @PostMapping("/cep")
    public RaceStatus evaluateCep(@RequestBody RaceStatus raceStatus) {
        return raceRuleService.evaluateCep(raceStatus);
    }

    @PostMapping("/violation-template")
    public DriverViolation evaluateViolationTemplate(@RequestBody DriverViolation violation) {
        return raceRuleService.evaluateViolationTemplate(violation);
    }
}
