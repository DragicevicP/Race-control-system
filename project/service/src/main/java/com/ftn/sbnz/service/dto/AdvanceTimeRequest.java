package com.ftn.sbnz.service.dto;

public class AdvanceTimeRequest {

    private long seconds;

    public AdvanceTimeRequest() {
    }

    public AdvanceTimeRequest(long seconds) {
        this.seconds = seconds;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }
}
