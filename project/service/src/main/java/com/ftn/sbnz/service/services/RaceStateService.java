package com.ftn.sbnz.service.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.enums.FlagType;
import com.ftn.sbnz.model.enums.PenaltyType;
import com.ftn.sbnz.model.enums.SafetyCarStatus;
import com.ftn.sbnz.model.enums.SessionStatus;
import com.ftn.sbnz.model.enums.TrackStatus;
import com.ftn.sbnz.model.models.Driver;
import com.ftn.sbnz.model.models.DriverViolation;
import com.ftn.sbnz.model.models.Penalty;
import com.ftn.sbnz.model.models.RaceControlDecision;
import com.ftn.sbnz.model.models.RaceStatus;
import com.ftn.sbnz.model.models.TrackSector;

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
        if (raceStatus.getViolations() == null) {
            raceStatus.setViolations(new ArrayList<>());
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
        updateTrackState(decision);
        updateSessionStatus(decision);

        return raceStatus;
    }

    public RaceStatus addDriverViolations(List<DriverViolation> violations) {
        if (raceStatus.getViolations() == null) {
            raceStatus.setViolations(new ArrayList<>());
        }

        for (DriverViolation violation : safeList(violations)) {
            normalizeAndAddViolation(violation);
        }

        return raceStatus;
    }

    public RaceStatus syncDriverViolationsToRaceStatus() {
        for (Driver driver : safeList(raceStatus.getDrivers())) {
            for (DriverViolation violation : safeList(driver.getViolations())) {
                normalizeAndAddViolation(violation);
            }
        }
        return raceStatus;
    }

    private RaceStatus createInitialState() {
        LocalDateTime demoTime = LocalDateTime.of(2026, 6, 28, 14, 12, 15);
        List<Driver> drivers = createDemoDrivers();

        RaceStatus initialState = new RaceStatus();
        initialState.setStatus(SessionStatus.GREEN_FLAG);
        initialState.setTrackStatus(TrackStatus.SAFE);
        initialState.setSectors(createDemoSectors());
        initialState.setDrivers(drivers);
        initialState.setViolations(new ArrayList<>());
        initialState.setBlueFlagMonitorings(new ArrayList<>());
        initialState.setVscDeltaMonitorings(new ArrayList<>());
        initialState.setCurrentDecision(null);
        initialState.setDecisionLog(new ArrayList<>());
        initialState.setSimulationTime(demoTime);
        return initialState;
    }

    private List<TrackSector> createDemoSectors() {
        return List.of(
                new TrackSector(1, false, false, false, false, false, false, FlagType.GREEN),
                new TrackSector(2, false, false, false, false, false, false, FlagType.GREEN),
                new TrackSector(3, false, false, false, false, false, false, FlagType.GREEN));
    }

    private List<Driver> createDemoDrivers() {
        return new ArrayList<>(List.of(
                driver(1L, "ANT", "Kimi Antonelli", "Mercedes-AMG Petronas F1 Team", 1, "Leader", 0, null),
                driver(2L, "HAM", "Lewis Hamilton", "Scuderia Ferrari HP", 2, "+1.284", 0, null),
                driver(3L, "RUS", "George Russell", "Mercedes-AMG Petronas F1 Team", 3, "+3.912", 0, null),
                driver(4L, "LEC", "Charles Leclerc", "Scuderia Ferrari HP", 4, "+5.408", 0, null),
                driver(5L, "NOR", "Lando Norris", "McLaren Mastercard F1 Team", 5, "+7.106", 0, null),
                driver(6L, "PIA", "Oscar Piastri", "McLaren Mastercard F1 Team", 6, "+8.774", 0, null),
                driver(7L, "VER", "Max Verstappen", "Oracle Red Bull Racing", 7, "+10.332", 0, null),
                driver(8L, "HAD", "Isack Hadjar", "Oracle Red Bull Racing", 8, "+13.615", 0, null),
                driver(9L, "SAI", "Carlos Sainz Jr.", "Atlassian Williams F1 Team", 9, "+18.902", 0, null),
                driver(10L, "ALB", "Alexander Albon", "Atlassian Williams F1 Team", 10, "+21.447", 0, null),
                driver(11L, "ALO", "Fernando Alonso", "Aston Martin Aramco F1 Team", 11, "+24.806", 0, null),
                driver(12L, "STR", "Lance Stroll", "Aston Martin Aramco F1 Team", 12, "+28.233", 0, null),
                driver(13L, "GAS", "Pierre Gasly", "BWT Alpine F1 Team", 13, "+31.991", 0, null),
                driver(14L, "COL", "Franco Colapinto", "BWT Alpine F1 Team", 14, "+35.420", 0, null),
                driver(15L, "HUL", "Nico Hulkenberg", "Audi Revolut F1 Team", 15, "+39.118", 0, null),
                driver(16L, "BOR", "Gabriel Bortoleto", "Audi Revolut F1 Team", 16, "+42.507", 0, null),
                driver(17L, "OCO", "Esteban Ocon", "TGR Haas F1 Team", 17, "+47.832", 0, null),
                driver(18L, "BEA", "Oliver Bearman", "TGR Haas F1 Team", 18, "+51.306", 0, null),
                driver(19L, "LAW", "Liam Lawson", "Racing Bulls F1 Team", 19, "+1 lap", 0, null),
                driver(20L, "LIN", "Arvid Lindblad", "Racing Bulls F1 Team", 20, "+1 lap", 0, null),
                driver(21L, "PER", "Sergio Perez", "Cadillac Formula 1 Team", 21, "+1 lap", 0, null),
                driver(22L, "BOT", "Valtteri Bottas", "Cadillac Formula 1 Team", 22, "+1 lap", 0, null)));
    }

    private Driver driver(Long id, String code, String name, String team, int position, String gap, int warningCount,
            Penalty activePenalty) {
        return new Driver(id, code, name, team, position, gap, new ArrayList<>(), warningCount, activePenalty);
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

    private void updateTrackState(RaceControlDecision decision) {
        FlagType recommendedFlag = decision.getRecommendedFlag();
        if (recommendedFlag == null || raceStatus.getSectors() == null) {
            return;
        }

        if (recommendedFlag == FlagType.GREEN) {
            raceStatus.setTrackStatus(TrackStatus.SAFE);
            for (TrackSector sector : raceStatus.getSectors()) {
                clearSector(sector, FlagType.GREEN);
            }
            return;
        }

        if (recommendedFlag == FlagType.RED) {
            raceStatus.setTrackStatus(TrackStatus.FULLY_BLOCKED);
            for (TrackSector sector : raceStatus.getSectors()) {
                sector.setActiveFlag(FlagType.RED);
            }
            return;
        }

        boolean globalRaceControlState = decision.getSafetyCarStatus() == SafetyCarStatus.VSC
                || decision.getSafetyCarStatus() == SafetyCarStatus.SAFETY_CAR;
        boolean updatedAnySector = false;

        for (TrackSector sector : raceStatus.getSectors()) {
            if (globalRaceControlState || isAffectedSector(sector)) {
                sector.setActiveFlag(recommendedFlag);
                updatedAnySector = true;
            }
        }

        if (!updatedAnySector && !raceStatus.getSectors().isEmpty()) {
            raceStatus.getSectors().get(0).setActiveFlag(recommendedFlag);
        }

        if (recommendedFlag == FlagType.DOUBLE_YELLOW || globalRaceControlState) {
            raceStatus.setTrackStatus(TrackStatus.PARTIALLY_BLOCKED);
        }
    }

    private boolean isAffectedSector(TrackSector sector) {
        return sector.isBlocked()
                || sector.isPartiallyBlocked()
                || sector.isHasDebris()
                || sector.isHasStoppedVehicle()
                || sector.isMarshalsOnTrack()
                || sector.isMedicalTeamOnTrack();
    }

    private void clearSector(TrackSector sector, FlagType flagType) {
        sector.setBlocked(false);
        sector.setPartiallyBlocked(false);
        sector.setHasDebris(false);
        sector.setHasStoppedVehicle(false);
        sector.setMarshalsOnTrack(false);
        sector.setMedicalTeamOnTrack(false);
        sector.setActiveFlag(flagType);
    }

    private void normalizeAndAddViolation(DriverViolation violation) {
        if (violation == null || alreadyTracked(violation)) {
            return;
        }

        if (violation.getId() == null) {
            violation.setId(nextViolationId());
        }
        if (violation.getTime() == null) {
            violation.setTime(raceStatus.getSimulationTime() != null ? raceStatus.getSimulationTime() : LocalDateTime.now());
        }

        Driver stateDriver = findStateDriver(violation.getDriver());
        if (stateDriver != null) {
            violation.setDriver(stateDriver);
            if (stateDriver.getViolations() == null) {
                stateDriver.setViolations(new ArrayList<>());
            }
            if (!containsViolation(stateDriver.getViolations(), violation)) {
                stateDriver.getViolations().add(violation);
            }
            applyPenaltyToDriver(stateDriver, violation);
        }

        raceStatus.getViolations().add(violation);
    }

    private boolean alreadyTracked(DriverViolation violation) {
        if (raceStatus.getViolations() == null) {
            raceStatus.setViolations(new ArrayList<>());
            return false;
        }
        return containsViolation(raceStatus.getViolations(), violation);
    }

    private boolean containsViolation(List<DriverViolation> violations, DriverViolation candidate) {
        for (DriverViolation violation : safeList(violations)) {
            if (sameViolation(violation, candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean sameViolation(DriverViolation first, DriverViolation second) {
        if (first.getId() != null && second.getId() != null) {
            return first.getId().equals(second.getId());
        }

        String firstDriverCode = first.getDriver() != null ? first.getDriver().getCode() : null;
        String secondDriverCode = second.getDriver() != null ? second.getDriver().getCode() : null;
        String firstViolationCode = first.getViolation() != null ? first.getViolation().getCode() : null;
        String secondViolationCode = second.getViolation() != null ? second.getViolation().getCode() : null;

        return firstDriverCode != null && firstDriverCode.equals(secondDriverCode)
                && firstViolationCode != null && firstViolationCode.equals(secondViolationCode)
                && first.getTime() != null && first.getTime().equals(second.getTime());
    }

    private Driver findStateDriver(Driver driver) {
        if (driver == null || raceStatus.getDrivers() == null) {
            return null;
        }

        for (Driver stateDriver : raceStatus.getDrivers()) {
            if (driver.getCode() != null && driver.getCode().equals(stateDriver.getCode())) {
                return stateDriver;
            }
            if (driver.getId() != null && driver.getId().equals(stateDriver.getId())) {
                return stateDriver;
            }
        }
        return null;
    }

    private void applyPenaltyToDriver(Driver driver, DriverViolation violation) {
        Penalty penalty = violation.getPenalty();
        if (penalty == null) {
            return;
        }

        if (penalty.getType() == PenaltyType.WARNING) {
            driver.setWarningCount(driver.getWarningCount() + 1);
        }
        driver.setActivePenalty(penalty);
    }

    private Long nextViolationId() {
        long maxId = 0L;
        for (DriverViolation violation : safeList(raceStatus.getViolations())) {
            if (violation.getId() != null && violation.getId() > maxId) {
                maxId = violation.getId();
            }
        }
        return maxId + 1;
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }
}
