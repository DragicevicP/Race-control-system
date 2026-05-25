package com.ftn.sbnz.model.models;


import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.model.enums.SessionStatus;
import com.ftn.sbnz.model.enums.TrackStatus;


public class RaceStatus {

    private Long id;
    private SessionStatus status;
    private TrackStatus trackStatus;
    private List<TrackSector> sectors = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();
    private List<Driver> drivers = new ArrayList<>();
    private RaceControlDecision currentDecision;
}