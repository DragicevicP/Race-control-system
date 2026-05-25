package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.enums.FlagType;


public class TrackSector {

    private int sectorNumber;
    private boolean blocked;
    private boolean partiallyBlocked;
    private boolean hasDebris;
    private boolean hasStoppedVehicle;
    private boolean marshalsOnTrack;
    private boolean medicalTeamOnTrack;
    private FlagType activeFlag;

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isMarshalsOnTrack() {
        return marshalsOnTrack;
    }

    public void setMarshalsOnTrack(boolean marshalsOnTrack) {
        this.marshalsOnTrack = marshalsOnTrack;
    }
}