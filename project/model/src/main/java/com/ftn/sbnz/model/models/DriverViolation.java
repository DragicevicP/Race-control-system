package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;

import com.ftn.sbnz.model.enums.ViolationSeverity;


public class DriverViolation {

    private Long id;
    private Driver driver;
    private Violation violation;
    private ViolationSeverity severity;
    private boolean repeated;
    private boolean safetyRisk;
    private Penalty penalty;
    private LocalDateTime time;
}