package com.ftn.sbnz.model.models;

import java.util.ArrayList;
import java.util.List;


public class Driver {

    private Long id;
    private String name;
    private String team;
    private List<DriverViolation> violations = new ArrayList<>();
    private int warningCount;
}