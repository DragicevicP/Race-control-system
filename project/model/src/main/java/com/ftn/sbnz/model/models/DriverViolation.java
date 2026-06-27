package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;

import com.ftn.sbnz.model.enums.ViolationSeverity;


public class DriverViolation {

    private Long id;
    private Driver driver;
    private Violation violation;
    private ViolationSeverity severity;
    private boolean repeated;
    private Penalty penalty;
    private LocalDateTime time;

    public DriverViolation() {
    }

    public DriverViolation(Long id, Driver driver, Violation violation, ViolationSeverity severity, boolean repeated,
            Penalty penalty, LocalDateTime time) {
        this.id = id;
        this.driver = driver;
        this.violation = violation;
        this.severity = severity;
        this.repeated = repeated;
        this.penalty = penalty;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }


}
