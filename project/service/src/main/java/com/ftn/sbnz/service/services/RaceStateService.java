package com.ftn.sbnz.service.services;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.enums.FlagType;
import com.ftn.sbnz.model.enums.SafetyCarStatus;
import com.ftn.sbnz.model.enums.SessionStatus;
import com.ftn.sbnz.model.models.RaceControlDecision;
import com.ftn.sbnz.model.models.RaceStatus;

@Service
public class RaceStateService {

    private RaceStatus raceStatus = createInitialState();

    public RaceStatus getRaceStatus() {
        return raceStatus;
    }

    public RaceStatus replaceRaceStatus(RaceStatus raceStatus) {
        if (raceStatus == null) {
            return resetRaceStatus();
        }
        if (raceStatus.getSimulationTime() == null) {
            raceStatus.setSimulationTime(LocalDateTime.now());
        }
        if (raceStatus.getDecisionLog() == null) {
            raceStatus.setDecisionLog(new ArrayList<>());
        }
        this.raceStatus = raceStatus;
        return this.raceStatus;
    }

    public RaceStatus resetRaceStatus() {
        this.raceStatus = createInitialState();
        return this.raceStatus;
    }

    public RaceStatus advanceSimulationTime(long seconds) {
        LocalDateTime currentTime = raceStatus.getSimulationTime();
        if (currentTime == null) {
            currentTime = LocalDateTime.now();
        }
        raceStatus.setSimulationTime(currentTime.plusSeconds(seconds));
        return raceStatus;
    }

    public RaceStatus applyDecision(RaceControlDecision decision) {
        if (decision == null) {
            return raceStatus;
        }
        if (decision.getCreatedAt() == null) {
            decision.setCreatedAt(raceStatus.getSimulationTime() != null
                    ? raceStatus.getSimulationTime()
                    : LocalDateTime.now());
        }
        decision.setApplied(true);

        raceStatus.setCurrentDecision(decision);
        if (raceStatus.getDecisionLog() == null) {
            raceStatus.setDecisionLog(new ArrayList<>());
        }
        raceStatus.getDecisionLog().add(decision);
        updateSessionStatus(decision);

        return raceStatus;
    }

    private RaceStatus createInitialState() {
        RaceStatus initialState = new RaceStatus();
        initialState.setStatus(SessionStatus.GREEN_FLAG);
        initialState.setSimulationTime(LocalDateTime.now());
        return initialState;
    }

    private void updateSessionStatus(RaceControlDecision decision) {
        if (decision.getRecommendedFlag() == FlagType.RED) {
            raceStatus.setStatus(SessionStatus.RED_FLAG);
            return;
        }

        if (decision.getSafetyCarStatus() == SafetyCarStatus.VSC) {
            raceStatus.setStatus(SessionStatus.VSC);
            return;
        }

        if (decision.getSafetyCarStatus() == SafetyCarStatus.SAFETY_CAR) {
            raceStatus.setStatus(SessionStatus.SAFETY_CAR);
            return;
        }

        if (decision.getRecommendedFlag() == FlagType.GREEN) {
            raceStatus.setStatus(SessionStatus.GREEN_FLAG);
        }
    }
}
