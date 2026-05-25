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

    public DangerLevel getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(DangerLevel dangerLevel) {
        this.dangerLevel = dangerLevel;
    }
}

