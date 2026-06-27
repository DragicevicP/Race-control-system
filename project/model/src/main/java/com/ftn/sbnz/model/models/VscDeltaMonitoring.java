package com.ftn.sbnz.model.models;


import java.time.LocalDateTime;

import com.ftn.sbnz.model.enums.DeltaStatus;

public class VscDeltaMonitoring {

    private Driver driver;
    private LocalDateTime vscActivatedAt;
    private DeltaStatus deltaStatus;
    private boolean active;

    public VscDeltaMonitoring() {
    }

    public VscDeltaMonitoring(Driver driver, LocalDateTime vscActivatedAt, DeltaStatus deltaStatus, boolean active) {
        this.driver = driver;
        this.vscActivatedAt = vscActivatedAt;
        this.deltaStatus = deltaStatus;
        this.active = active;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public LocalDateTime getVscActivatedAt() {
        return vscActivatedAt;
    }

    public void setVscActivatedAt(LocalDateTime vscActivatedAt) {
        this.vscActivatedAt = vscActivatedAt;
    }

    public DeltaStatus getDeltaStatus() {
        return deltaStatus;
    }

    public void setDeltaStatus(DeltaStatus deltaStatus) {
        this.deltaStatus = deltaStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
