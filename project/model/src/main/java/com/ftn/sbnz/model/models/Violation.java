package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.enums.PenaltyType;
import com.ftn.sbnz.model.enums.ViolationSeverity;


public class Violation {

    private String code;
    private String name;
    private String description;
    private ViolationSeverity defaultSeverity;
    private PenaltyType defaultPenaltyType;
}