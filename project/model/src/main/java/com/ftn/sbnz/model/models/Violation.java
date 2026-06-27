package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.enums.PenaltyType;
import com.ftn.sbnz.model.enums.ViolationSeverity;


public class Violation {

    private String code;
    private String name;
    private String description;
    private ViolationSeverity defaultSeverity;
    private PenaltyType defaultPenaltyType;

    public Violation() {
    }

    public Violation(String code, String name, String description, ViolationSeverity defaultSeverity,
            PenaltyType defaultPenaltyType) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.defaultSeverity = defaultSeverity;
        this.defaultPenaltyType = defaultPenaltyType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ViolationSeverity getDefaultSeverity() {
        return defaultSeverity;
    }

    public void setDefaultSeverity(ViolationSeverity defaultSeverity) {
        this.defaultSeverity = defaultSeverity;
    }

    public PenaltyType getDefaultPenaltyType() {
        return defaultPenaltyType;
    }

    public void setDefaultPenaltyType(PenaltyType defaultPenaltyType) {
        this.defaultPenaltyType = defaultPenaltyType;
    }
}
