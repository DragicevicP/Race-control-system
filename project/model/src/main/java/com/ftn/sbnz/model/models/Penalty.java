package com.ftn.sbnz.model.models;

import com.ftn.sbnz.model.enums.PenaltyType;


public class Penalty {

    private PenaltyType type;
    private Float seconds;
    private String reason;

    public Penalty() {
    }

    public Penalty(PenaltyType type, Float seconds, String reason) {
        this.type = type;
        this.seconds = seconds;
        this.reason = reason;
    }

    public PenaltyType getType() {
        return type;
    }

    public void setType(PenaltyType type) {
        this.type = type;
    }

    public Float getSeconds() {
        return seconds;
    }

    public void setSeconds(Float seconds) {
        this.seconds = seconds;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
