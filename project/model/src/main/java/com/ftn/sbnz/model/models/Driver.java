package com.ftn.sbnz.model.models;

import java.util.ArrayList;
import java.util.List;


public class Driver {

    private Long id;
    private String name;
    private String team;
    private int position;
    private String gap;
    private List<DriverViolation> violations = new ArrayList<>();
    private int warningCount;
    private Penalty activePenalty;

    public Driver() {
    }

    public Driver(Long id, String name, String team, int position, String gap, List<DriverViolation> violations,
            int warningCount, Penalty activePenalty) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.position = position;
        this.gap = gap;
        this.violations = violations;
        this.warningCount = warningCount;
        this.activePenalty = activePenalty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }

    public List<DriverViolation> getViolations() {
        return violations;
    }

    public void setViolations(List<DriverViolation> violations) {
        this.violations = violations;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public Penalty getActivePenalty() {
        return activePenalty;
    }

    public void setActivePenalty(Penalty activePenalty) {
        this.activePenalty = activePenalty;
    }
}
