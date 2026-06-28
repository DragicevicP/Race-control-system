package com.ftn.sbnz.service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.models.RaceControlDecision;
import com.ftn.sbnz.model.models.RaceStatus;
import com.ftn.sbnz.service.dto.AdvanceTimeRequest;
import com.ftn.sbnz.service.dto.DriverBehaviorEvaluationRequest;
import com.ftn.sbnz.service.services.RaceRuleService;
import com.ftn.sbnz.service.services.RaceStateService;

@RestController
@RequestMapping("/api/race")
public class RaceStateController {

    private final RaceStateService raceStateService;
    private final RaceRuleService raceRuleService;

    public RaceStateController(RaceStateService raceStateService, RaceRuleService raceRuleService) {
        this.raceStateService = raceStateService;
        this.raceRuleService = raceRuleService;
    }

    @GetMapping("/state")
    public RaceStatus getRaceStatus() {
        return raceStateService.getRaceStatus();
    }

    @PutMapping("/state")
    public RaceStatus replaceRaceStatus(@RequestBody RaceStatus raceStatus) {
        return raceStateService.replaceRaceStatus(raceStatus);
    }

    @PostMapping("/reset")
    public RaceStatus resetRaceStatus() {
        return raceStateService.resetRaceStatus();
    }

    @PostMapping("/evaluate")
    public RaceControlDecision evaluateCurrentRaceStatus() {
        return raceRuleService.evaluateRaceStatus(raceStateService.getRaceStatus());
    }

    @PostMapping("/apply-decision")
    public RaceStatus applyDecision(@RequestBody RaceControlDecision decision) {
        return raceStateService.applyDecision(decision);
    }

    @PostMapping("/clock/advance")
    public RaceStatus advanceSimulationTime(@RequestBody AdvanceTimeRequest request) {
        long seconds = request != null ? request.getSeconds() : 0;
        RaceStatus raceStatus = raceStateService.advanceSimulationTime(seconds);
        raceRuleService.evaluateCep(raceStatus);
        return raceStateService.syncDriverViolationsToRaceStatus();
    }

    @PostMapping("/driver-behavior")
    public RaceStatus evaluateDriverBehavior(@RequestBody DriverBehaviorEvaluationRequest request) {
        if (request == null) {
            return raceStateService.getRaceStatus();
        }
        return raceStateService.addDriverViolations(
                raceRuleService.evaluateDriverBehavior(request.getReport(), request.getPreviousViolations()));
    }

    @PostMapping("/cep/evaluate")
    public RaceStatus evaluateCep() {
        raceRuleService.evaluateCep(raceStateService.getRaceStatus());
        return raceStateService.syncDriverViolationsToRaceStatus();
    }
}
