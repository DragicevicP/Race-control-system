package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;


public class BlueFlagMonitoring {

    private Driver driver;
    private LocalDateTime blueFlagShownAt;
    private int warningCount;
    private boolean passedFasterCar;
    private boolean active;

    public BlueFlagMonitoring() {
    }

    public BlueFlagMonitoring(Driver driver, LocalDateTime blueFlagShownAt, int warningCount,
            boolean passedFasterCar, boolean active) {
        this.driver = driver;
        this.blueFlagShownAt = blueFlagShownAt;
        this.warningCount = warningCount;
        this.passedFasterCar = passedFasterCar;
        this.active = active;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public LocalDateTime getBlueFlagShownAt() {
        return blueFlagShownAt;
    }

    public void setBlueFlagShownAt(LocalDateTime blueFlagShownAt) {
        this.blueFlagShownAt = blueFlagShownAt;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public boolean isPassedFasterCar() {
        return passedFasterCar;
    }

    public void setPassedFasterCar(boolean passedFasterCar) {
        this.passedFasterCar = passedFasterCar;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
