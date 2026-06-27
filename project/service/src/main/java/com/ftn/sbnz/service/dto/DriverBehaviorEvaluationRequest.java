package com.ftn.sbnz.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.model.models.DriverBehaviorReport;
import com.ftn.sbnz.model.models.DriverViolation;

public class DriverBehaviorEvaluationRequest {

    private DriverBehaviorReport report;
    private List<DriverViolation> previousViolations = new ArrayList<>();

    public DriverBehaviorEvaluationRequest() {
    }

    public DriverBehaviorEvaluationRequest(DriverBehaviorReport report, List<DriverViolation> previousViolations) {
        this.report = report;
        this.previousViolations = previousViolations;
    }

    public DriverBehaviorReport getReport() {
        return report;
    }

    public void setReport(DriverBehaviorReport report) {
        this.report = report;
    }

    public List<DriverViolation> getPreviousViolations() {
        return previousViolations;
    }

    public void setPreviousViolations(List<DriverViolation> previousViolations) {
        this.previousViolations = previousViolations;
    }
}
