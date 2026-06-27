package com.ftn.sbnz.service.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.models.DriverViolation;
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
        return raceRuleService.evaluateCep(raceStatus);
    }

    @PostMapping("/driver-behavior")
    public List<DriverViolation> evaluateDriverBehavior(@RequestBody DriverBehaviorEvaluationRequest request) {
        if (request == null) {
            return List.of();
        }
        return raceRuleService.evaluateDriverBehavior(request.getReport(), request.getPreviousViolations());
    }

    @PostMapping("/cep/evaluate")
    public RaceStatus evaluateCep() {
        return raceRuleService.evaluateCep(raceStateService.getRaceStatus());
    }
}
