package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;

import javax.print.attribute.standard.Severity;

import com.ftn.sbnz.model.enums.ViolationSeverity;


public class DriverViolation {

    private Long id;
    private Driver driver;
    private Violation violation;
    private ViolationSeverity severity;
    private boolean repeated;
    private Penalty penalty;
    private LocalDateTime time;


    public Violation getViolation() {
        return violation;
    }

    public void setViolation(Violation violation) {
        this.violation = violation;
    }

    public ViolationSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ViolationSeverity severity) {
        this.severity = severity;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }


}