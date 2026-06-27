package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.enums.DangerLevel;
import com.ftn.sbnz.model.enums.IncidentType;
import com.ftn.sbnz.model.enums.TrackPosition;




public class Incident {

    private Long id;
    private IncidentType type;
    private TrackSector sector;
    private TrackPosition position;
    private boolean multipleSectors;
    private boolean driverLeftCar;
    private boolean fluidLeak;
    private boolean resolved;
    private DangerLevel dangerLevel;

    public Incident() {
    }

    public Incident(Long id, IncidentType type, TrackSector sector, TrackPosition position, boolean multipleSectors,
            boolean driverLeftCar, boolean fluidLeak, boolean resolved, DangerLevel dangerLevel) {
        this.id = id;
        this.type = type;
        this.sector = sector;
        this.position = position;
        this.multipleSectors = multipleSectors;
        this.driverLeftCar = driverLeftCar;
        this.fluidLeak = fluidLeak;
        this.resolved = resolved;
        this.dangerLevel = dangerLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IncidentType getType() {
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
    }

    public TrackSector getSector() {
        return sector;
    }

    public void setSector(TrackSector sector) {
        this.sector = sector;
    }

    public TrackPosition getPosition() {
        return position;
    }

    public void setPosition(TrackPosition position) {
        this.position = position;
    }

    public boolean isMultipleSectors() {
        return multipleSectors;
    }

    public void setMultipleSectors(boolean multipleSectors) {
        this.multipleSectors = multipleSectors;
    }

    public boolean isDriverLeftCar() {
        return driverLeftCar;
    }

    public void setDriverLeftCar(boolean driverLeftCar) {
        this.driverLeftCar = driverLeftCar;
    }

    public boolean isFluidLeak() {
        return fluidLeak;
    }

    public void setFluidLeak(boolean fluidLeak) {
        this.fluidLeak = fluidLeak;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public DangerLevel getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(DangerLevel dangerLevel) {
        this.dangerLevel = dangerLevel;
    }
}

