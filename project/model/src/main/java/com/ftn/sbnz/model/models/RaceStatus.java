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

    public RaceStatus() {
    }

    public RaceStatus(Long id, SessionStatus status, TrackStatus trackStatus, List<TrackSector> sectors,
            List<Incident> incidents, List<Driver> drivers, RaceControlDecision currentDecision) {
        this.id = id;
        this.status = status;
        this.trackStatus = trackStatus;
        this.sectors = sectors;
        this.incidents = incidents;
        this.drivers = drivers;
        this.currentDecision = currentDecision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public TrackStatus getTrackStatus() {
        return trackStatus;
    }

    public void setTrackStatus(TrackStatus trackStatus) {
        this.trackStatus = trackStatus;
    }

    public List<TrackSector> getSectors() {
        return sectors;
    }

    public void setSectors(List<TrackSector> sectors) {
        this.sectors = sectors;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<Incident> incidents) {
        this.incidents = incidents;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public RaceControlDecision getCurrentDecision() {
        return currentDecision;
    }

    public void setCurrentDecision(RaceControlDecision currentDecision) {
        this.currentDecision = currentDecision;
    }
}
