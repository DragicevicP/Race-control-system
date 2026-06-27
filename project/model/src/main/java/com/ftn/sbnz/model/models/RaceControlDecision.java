package com.ftn.sbnz.model.models;

import java.time.LocalDateTime;
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
    private boolean applied;
    private LocalDateTime createdAt;

    public RaceControlDecision() {
    }

    public RaceControlDecision(FlagType recommendedFlag, SafetyCarStatus safetyCarStatus, RestartStatus restartStatus,
            Penalty recommendedPenalty, String explanation, List<String> reasons, boolean applied,
            LocalDateTime createdAt) {
        this.recommendedFlag = recommendedFlag;
        this.safetyCarStatus = safetyCarStatus;
        this.restartStatus = restartStatus;
        this.recommendedPenalty = recommendedPenalty;
        this.explanation = explanation;
        this.reasons = reasons;
        this.applied = applied;
        this.createdAt = createdAt;
    }

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

    public RestartStatus getRestartStatus() {
        return restartStatus;
    }

    public void setRestartStatus(RestartStatus restartStatus) {
        this.restartStatus = restartStatus;
    }

    public Penalty getRecommendedPenalty() {
        return recommendedPenalty;
    }

    public void setRecommendedPenalty(Penalty recommendedPenalty) {
        this.recommendedPenalty = recommendedPenalty;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
