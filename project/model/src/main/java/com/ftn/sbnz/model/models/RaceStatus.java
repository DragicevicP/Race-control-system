package com.ftn.sbnz.model.models;


import java.time.LocalDateTime;
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
    private List<BlueFlagMonitoring> blueFlagMonitorings = new ArrayList<>();
    private List<VscDeltaMonitoring> vscDeltaMonitorings = new ArrayList<>();
    private RaceControlDecision currentDecision;
    private List<RaceControlDecision> decisionLog = new ArrayList<>();
    private LocalDateTime simulationTime;

    public RaceStatus() {
    }

    public RaceStatus(Long id, SessionStatus status, TrackStatus trackStatus, List<TrackSector> sectors,
            List<Incident> incidents, List<Driver> drivers, List<BlueFlagMonitoring> blueFlagMonitorings,
            List<VscDeltaMonitoring> vscDeltaMonitorings, RaceControlDecision currentDecision,
            List<RaceControlDecision> decisionLog, LocalDateTime simulationTime) {
        this.id = id;
        this.status = status;
        this.trackStatus = trackStatus;
        this.sectors = sectors;
        this.incidents = incidents;
        this.drivers = drivers;
        this.blueFlagMonitorings = blueFlagMonitorings;
        this.vscDeltaMonitorings = vscDeltaMonitorings;
        this.currentDecision = currentDecision;
        this.decisionLog = decisionLog;
        this.simulationTime = simulationTime;
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

    public List<BlueFlagMonitoring> getBlueFlagMonitorings() {
        return blueFlagMonitorings;
    }

    public void setBlueFlagMonitorings(List<BlueFlagMonitoring> blueFlagMonitorings) {
        this.blueFlagMonitorings = blueFlagMonitorings;
    }

    public List<VscDeltaMonitoring> getVscDeltaMonitorings() {
        return vscDeltaMonitorings;
    }

    public void setVscDeltaMonitorings(List<VscDeltaMonitoring> vscDeltaMonitorings) {
        this.vscDeltaMonitorings = vscDeltaMonitorings;
    }

    public RaceControlDecision getCurrentDecision() {
        return currentDecision;
    }

    public void setCurrentDecision(RaceControlDecision currentDecision) {
        this.currentDecision = currentDecision;
    }

    public List<RaceControlDecision> getDecisionLog() {
        return decisionLog;
    }

    public void setDecisionLog(List<RaceControlDecision> decisionLog) {
        this.decisionLog = decisionLog;
    }

    public LocalDateTime getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(LocalDateTime simulationTime) {
        this.simulationTime = simulationTime;
    }
}
