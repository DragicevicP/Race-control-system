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

    public TrackSector() {
    }

    public TrackSector(int sectorNumber, boolean blocked, boolean partiallyBlocked, boolean hasDebris,
            boolean hasStoppedVehicle, boolean marshalsOnTrack, boolean medicalTeamOnTrack, FlagType activeFlag) {
        this.sectorNumber = sectorNumber;
        this.blocked = blocked;
        this.partiallyBlocked = partiallyBlocked;
        this.hasDebris = hasDebris;
        this.hasStoppedVehicle = hasStoppedVehicle;
        this.marshalsOnTrack = marshalsOnTrack;
        this.medicalTeamOnTrack = medicalTeamOnTrack;
        this.activeFlag = activeFlag;
    }

    public int getSectorNumber() {
        return sectorNumber;
    }

    public void setSectorNumber(int sectorNumber) {
        this.sectorNumber = sectorNumber;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isPartiallyBlocked() {
        return partiallyBlocked;
    }

    public void setPartiallyBlocked(boolean partiallyBlocked) {
        this.partiallyBlocked = partiallyBlocked;
    }

    public boolean isHasDebris() {
        return hasDebris;
    }

    public void setHasDebris(boolean hasDebris) {
        this.hasDebris = hasDebris;
    }

    public boolean isHasStoppedVehicle() {
        return hasStoppedVehicle;
    }

    public void setHasStoppedVehicle(boolean hasStoppedVehicle) {
        this.hasStoppedVehicle = hasStoppedVehicle;
    }

    public boolean isMarshalsOnTrack() {
        return marshalsOnTrack;
    }

    public void setMarshalsOnTrack(boolean marshalsOnTrack) {
        this.marshalsOnTrack = marshalsOnTrack;
    }

    public boolean isMedicalTeamOnTrack() {
        return medicalTeamOnTrack;
    }

    public void setMedicalTeamOnTrack(boolean medicalTeamOnTrack) {
        this.medicalTeamOnTrack = medicalTeamOnTrack;
    }

    public FlagType getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(FlagType activeFlag) {
        this.activeFlag = activeFlag;
    }
}
