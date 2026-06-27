package com.ftn.sbnz.model.models;

import java.util.ArrayList;
import java.util.List;


public class Driver {

    private Long id;
    private String name;
    private String team;
    private List<DriverViolation> violations = new ArrayList<>();
    private int warningCount;

    public Driver() {
    }

    public Driver(Long id, String name, String team, List<DriverViolation> violations, int warningCount) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.violations = violations;
        this.warningCount = warningCount;
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
}
