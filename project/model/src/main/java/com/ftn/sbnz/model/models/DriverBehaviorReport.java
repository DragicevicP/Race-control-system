package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;

import com.ftn.sbnz.model.enums.FlagType;
import com.ftn.sbnz.model.enums.SafetyCarStatus;

public class DriverBehaviorReport {

    private Driver driver;
    private FlagType activeFlag;
    private SafetyCarStatus safetyCarStatus;
    private boolean didNotSlow;
    private boolean overtook;
    private boolean unsafeRelease;
    private boolean pitSpeeding;
    private boolean dangerousDriving;
    private boolean causingCollision;
    private int trackLimitsCount;
    private LocalDateTime time;
    private boolean repeatedRequested;
    private boolean processed;

    public DriverBehaviorReport() {
    }

    public DriverBehaviorReport(Driver driver, FlagType activeFlag, SafetyCarStatus safetyCarStatus,
            boolean didNotSlow, boolean overtook, boolean unsafeRelease, boolean pitSpeeding,
            boolean dangerousDriving, boolean causingCollision, int trackLimitsCount, LocalDateTime time,
            boolean repeatedRequested, boolean processed) {
        this.driver = driver;
        this.activeFlag = activeFlag;
        this.safetyCarStatus = safetyCarStatus;
        this.didNotSlow = didNotSlow;
        this.overtook = overtook;
        this.unsafeRelease = unsafeRelease;
        this.pitSpeeding = pitSpeeding;
        this.dangerousDriving = dangerousDriving;
        this.causingCollision = causingCollision;
        this.trackLimitsCount = trackLimitsCount;
        this.time = time;
        this.repeatedRequested = repeatedRequested;
        this.processed = processed;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public FlagType getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(FlagType activeFlag) {
        this.activeFlag = activeFlag;
    }

    public SafetyCarStatus getSafetyCarStatus() {
        return safetyCarStatus;
    }

    public void setSafetyCarStatus(SafetyCarStatus safetyCarStatus) {
        this.safetyCarStatus = safetyCarStatus;
    }

    public boolean isDidNotSlow() {
        return didNotSlow;
    }

    public void setDidNotSlow(boolean didNotSlow) {
        this.didNotSlow = didNotSlow;
    }

    public boolean isOvertook() {
        return overtook;
    }

    public void setOvertook(boolean overtook) {
        this.overtook = overtook;
    }

    public boolean isUnsafeRelease() {
        return unsafeRelease;
    }

    public void setUnsafeRelease(boolean unsafeRelease) {
        this.unsafeRelease = unsafeRelease;
    }

    public boolean isPitSpeeding() {
        return pitSpeeding;
    }

    public void setPitSpeeding(boolean pitSpeeding) {
        this.pitSpeeding = pitSpeeding;
    }

    public boolean isDangerousDriving() {
        return dangerousDriving;
    }

    public void setDangerousDriving(boolean dangerousDriving) {
        this.dangerousDriving = dangerousDriving;
    }

    public boolean isCausingCollision() {
        return causingCollision;
    }

    public void setCausingCollision(boolean causingCollision) {
        this.causingCollision = causingCollision;
    }

    public int getTrackLimitsCount() {
        return trackLimitsCount;
    }

    public void setTrackLimitsCount(int trackLimitsCount) {
        this.trackLimitsCount = trackLimitsCount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isRepeatedRequested() {
        return repeatedRequested;
    }

    public void setRepeatedRequested(boolean repeatedRequested) {
        this.repeatedRequested = repeatedRequested;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
