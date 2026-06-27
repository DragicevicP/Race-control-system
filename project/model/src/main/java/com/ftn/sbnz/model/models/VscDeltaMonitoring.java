package com.ftn.sbnz.model.models;


import java.time.LocalDateTime;

import com.ftn.sbnz.model.enums.DeltaStatus;

public class VscDeltaMonitoring {

    private Driver driver;
    private LocalDateTime vscActivatedAt;
    private DeltaStatus deltaStatus;
    private LocalDateTime deltaRedSince;
    private boolean active;

    public VscDeltaMonitoring() {
    }

    public VscDeltaMonitoring(Driver driver, LocalDateTime vscActivatedAt, DeltaStatus deltaStatus,
            LocalDateTime deltaRedSince, boolean active) {
        this.driver = driver;
        this.vscActivatedAt = vscActivatedAt;
        this.deltaStatus = deltaStatus;
        this.deltaRedSince = deltaRedSince;
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

    public LocalDateTime getDeltaRedSince() {
        return deltaRedSince;
    }

    public void setDeltaRedSince(LocalDateTime deltaRedSince) {
        this.deltaRedSince = deltaRedSince;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
