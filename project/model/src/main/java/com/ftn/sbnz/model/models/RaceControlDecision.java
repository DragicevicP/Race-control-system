package com.ftn.sbnz.model.models;

import java.util.ArrayList;
import java.util.List;

import com.ftn.sbnz.model.enums.FlagType;
import com.ftn.sbnz.model.enums.RestartStatus;
import com.ftn.sbnz.model.enums.SafetyCarStatus;


public class RaceControlDecision {

    private FlagType recommendedFlag;
    private SafetyCarStatus safetyCarStatus;
    private RestartStatus restartStatus;
    private Penalty recommendedPenalty;
    private String explanation;
    private List<String> reasons = new ArrayList<>();

    public FlagType getRecommendedFlag() {
        return recommendedFlag;
    }

    public void setRecommendedFlag(FlagType recommendedFlag) {
        this.recommendedFlag = recommendedFlag;
    }

    public SafetyCarStatus getSafetyCarStatus() {
        return safetyCarStatus;
    }

    public void setSafetyCarStatus(SafetyCarStatus safetyCarStatus) {
        this.safetyCarStatus = safetyCarStatus;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}