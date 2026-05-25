package com.ftn.sbnz.service.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.models.Incident;
import com.ftn.sbnz.model.models.RaceControlDecision;
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
}