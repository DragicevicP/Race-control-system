import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  BlueFlagMonitoring,
  Driver,
  DriverBehaviorReport,
  FlagType,
  Incident,
  IncidentType,
  RaceControlDecision,
  RaceStatus,
  SafetyCarStatus,
  TrackPosition,
  TrackSector,
  VscDeltaMonitoring,
} from '../../models/race-control.models';
import { RaceControlService } from '../../services/race-control.service';

interface SectorView {
  id: number;
  name: string;
  status: string;
  note: string;
}

interface DriverView {
  position: number;
  code: string;
  team: string;
  gap: string;
  warnings: number;
  penalty: string;
}

@Component({
  selector: 'app-race-control-page',
  imports: [FormsModule],
  templateUrl: './race-control-page.html',
  styleUrl: './race-control-page.css',
})
export class RaceControlPage implements OnInit, OnDestroy {
  protected raceStatus?: RaceStatus;
  protected currentDecision?: RaceControlDecision;
  protected loading = false;
  protected errorMessage = '';
  protected clockRunning = false;
  private clockTimer?: ReturnType<typeof setInterval>;
  private clockAdvanceInFlight = false;
  private vscDeltaSyncInFlight = false;
  private stateMutationVersion = 0;

  protected incidentForm = {
    type: 'DEBRIS' as IncidentType,
    sectorNumber: 2,
    position: 'RACING_LINE' as TrackPosition,
    blocked: false,
    marshals: true,
    medicalTeam: false,
    multipleSectors: false,
    driverLeftCar: false,
  };

  protected recoveryForm = {
    sectorNumber: 1,
    blocked: true,
    debris: true,
    stoppedVehicle: true,
    marshals: true,
    medicalTeam: true,
  };

  protected violationForm = {
    driverCode: 'LEC',
    violationType: 'TRACK_LIMITS',
    sectorNumber: 2,
    trackLimitsCount: 1,
    repeated: false,
  };

  protected blueFlagForm = {
    driverCode: 'PER',
  };

  protected readonly incidentTypes = [
    { value: 'STOPPED_VEHICLE', label: 'Stopped vehicle' },
    { value: 'CRASH', label: 'Crash' },
    { value: 'DEBRIS', label: 'Debris' },
    { value: 'WEATHER', label: 'Extreme weather' },
    { value: 'FLUID_LEAK', label: 'Fluid leak' },
  ] as const;

  protected readonly trackPositions = [
    { value: 'RACING_LINE', label: 'Racing line' },
    { value: 'TRACK_EDGE', label: 'Track edge' },
    { value: 'OFF_RACING_LINE', label: 'Off racing line' },
  ] as const;

  protected readonly violationTypes = [
    { value: 'TRACK_LIMITS', label: 'Track limits' },
    { value: 'OVERTAKING_YELLOW', label: 'Overtaking under yellow' },
    { value: 'DID_NOT_SLOW_YELLOW', label: 'Did not slow under yellow' },
    { value: 'OVERTAKING_DOUBLE_YELLOW', label: 'Overtaking under double yellow' },
    { value: 'DID_NOT_SLOW_DOUBLE_YELLOW', label: 'Did not slow under double yellow' },
    { value: 'OVERTAKING_SAFETY_CAR', label: 'Overtaking under safety car' },
    { value: 'SAFETY_CAR_INFRINGEMENT', label: 'Safety car infringement' },
    { value: 'UNSAFE_RELEASE', label: 'Unsafe release' },
    { value: 'PIT_SPEEDING', label: 'Pit speeding' },
    { value: 'CAUSING_COLLISION', label: 'Causing collision' },
    { value: 'DANGEROUS_DRIVING', label: 'Dangerous driving' },
  ] as const;

  constructor(private readonly raceControlService: RaceControlService) {}

  ngOnInit(): void {
    this.loadRaceState();
  }

  ngOnDestroy(): void {
    this.stopClock();
  }

  protected get sectors(): SectorView[] {
    return (this.raceStatus?.sectors ?? []).map((sector) => ({
      id: sector.sectorNumber,
      name: `Sector ${sector.sectorNumber}`,
      status: sector.activeFlag ?? 'GREEN',
      note: this.sectorNote(sector),
    }));
  }

  protected get drivers(): DriverView[] {
    return [...(this.raceStatus?.drivers ?? [])]
      .sort((a, b) => (a.position ?? 0) - (b.position ?? 0))
      .map((driver) => ({
        position: driver.position ?? 0,
        code: driver.code ?? '-',
        team: this.shortTeam(driver.team),
        gap: driver.gap ?? '-',
        warnings: (driver.warningCount ?? 0) + this.activeBlueFlagWarnings(driver.code),
        penalty: this.driverPenaltyLabel(driver),
      }));
  }

  protected get events(): string[] {
    const decisions = (this.raceStatus?.decisionLog ?? []).map((decision) =>
      `${this.formatTime(decision.createdAt)} ${decision.explanation ?? 'Decision updated'}`
    );
    const violations = (this.raceStatus?.violations ?? []).map((violation) =>
      `${this.formatTime(violation.time)} ${violation.driver?.code ?? 'Driver'}: ${violation.violation?.name ?? 'Violation'}`
    );
    return [...decisions, ...violations].slice(-8).reverse();
  }

  protected get selectedDecision(): RaceControlDecision | undefined {
    return this.currentDecision ?? this.raceStatus?.currentDecision ?? undefined;
  }

  protected get sessionStatus(): string {
    return this.raceStatus?.status ?? '-';
  }

  protected get sessionLabel(): string {
    return this.formatEnumLabel(this.sessionStatus);
  }

  protected get flagStatus(): string {
    if (this.selectedDecision?.recommendedFlag) {
      return this.selectedDecision.recommendedFlag;
    }
    if (this.selectedDecision?.restartStatus) {
      return this.selectedDecision.restartStatus === 'RACE_RESTARTED' ? 'GREEN' : this.primarySectorFlag();
    }
    return this.primarySectorFlag();
  }

  protected get flagLabel(): string {
    return this.formatEnumLabel(this.flagStatus);
  }

  protected get safetyCarStatus(): string {
    return this.selectedDecision?.safetyCarStatus ?? 'NONE';
  }

  protected get safetyCarLabel(): string {
    return this.formatEnumLabel(this.safetyCarStatus);
  }

  protected get simulationClock(): string {
    return this.formatTime(this.raceStatus?.simulationTime);
  }

  protected get driverOptions(): Driver[] {
    return this.raceStatus?.drivers ?? [];
  }

  protected get sectorOptions(): TrackSector[] {
    return this.raceStatus?.sectors ?? [];
  }

  protected get blueFlagMonitorLabel(): string {
    const active = (this.raceStatus?.blueFlagMonitorings ?? []).find((monitoring) => monitoring.active);
    if (active?.driver?.code) {
      return `${active.driver.code} active, warnings ${active.warningCount ?? 0}`;
    }

    const monitorings = this.raceStatus?.blueFlagMonitorings ?? [];
    const latest = monitorings[monitorings.length - 1];
    return latest?.driver?.code ? `${latest.driver.code} inactive, warnings ${latest.warningCount ?? 0}` : 'None active';
  }

  protected get activeBlueFlagMonitorings(): BlueFlagMonitoring[] {
    return (this.raceStatus?.blueFlagMonitorings ?? []).filter((monitoring) => monitoring.active);
  }

  protected get selectedDriverHasActiveBlueFlag(): boolean {
    return this.activeBlueFlagMonitorings.some(
      (monitoring) => monitoring.driver?.code === this.blueFlagForm.driverCode
    );
  }

  protected get vscDeltaMonitorLabel(): string {
    const active = (this.raceStatus?.vscDeltaMonitorings ?? []).find((monitoring) => monitoring.active);
    return active?.driver?.code ? `${active.driver.code} ${active.deltaStatus?.toLowerCase() ?? ''}` : 'None active';
  }

  protected get activeVscDeltaMonitorings(): VscDeltaMonitoring[] {
    return [...(this.raceStatus?.vscDeltaMonitorings ?? [])]
      .filter((monitoring) => monitoring.active)
      .sort((a, b) => (a.driver?.position ?? 0) - (b.driver?.position ?? 0));
  }

  protected get isVscCurrentlyActive(): boolean {
    return this.raceStatus?.status === 'VSC';
  }

  protected loadRaceState(): void {
    this.loading = true;
    this.raceControlService.getRaceState().subscribe({
      next: (raceStatus) => {
        this.setRaceState(raceStatus);
        this.startClock();
      },
      error: () => this.showError('Backend state could not be loaded.'),
    });
  }

  protected evaluateSituation(): void {
    const raceStatus = this.cloneState();
    const sector = this.incidentUsesLocation ? this.findSector(raceStatus, this.incidentForm.sectorNumber) : undefined;
    if (this.incidentUsesLocation && !sector) {
      this.showError('Selected sector does not exist.');
      return;
    }

    const hasDebris = this.incidentForm.type === 'DEBRIS';
    const hasFluidLeak = this.incidentForm.type === 'FLUID_LEAK';
    const hasStoppedVehicle = this.incidentForm.type === 'STOPPED_VEHICLE';
    if (sector) {
      sector.blocked = this.incidentForm.blocked;
      sector.partiallyBlocked = !this.incidentForm.blocked
        && (hasDebris || hasFluidLeak || hasStoppedVehicle || this.incidentForm.marshals);
      sector.hasDebris = hasDebris;
      sector.hasStoppedVehicle = hasStoppedVehicle;
      sector.marshalsOnTrack = this.incidentForm.marshals;
      sector.medicalTeamOnTrack = this.incidentShowsMedicalTeam && this.incidentForm.medicalTeam;
    }

    const incident: Incident = {
      id: Date.now(),
      type: this.incidentForm.type,
      sector,
      position: this.incidentUsesLocation ? this.incidentForm.position : undefined,
      multipleSectors: this.incidentForm.multipleSectors,
      driverLeftCar: this.incidentForm.driverLeftCar,
      fluidLeak: hasFluidLeak,
      resolved: false,
    };

    raceStatus.incidents = [...(raceStatus.incidents ?? []), incident];
    this.replaceStateThenEvaluate(raceStatus);
  }

  protected clearSelectedSector(): void {
    const raceStatus = this.cloneState();
    const sector = this.findSector(raceStatus, this.recoveryForm.sectorNumber);
    if (!sector) {
      this.showError('Selected recovery sector does not exist.');
      return;
    }

    this.applyRecoveryToSector(sector, raceStatus);
    raceStatus.incidents = (raceStatus.incidents ?? []).map((incident) =>
      incident.sector?.sectorNumber === sector.sectorNumber && this.isSectorClear(sector)
        ? { ...incident, sector: { ...sector }, resolved: true }
        : incident
    );
    this.normalizeTrackStatus(raceStatus);
    this.replaceStateThenEvaluate(raceStatus);
  }

  protected resolveAllIncidents(): void {
    const raceStatus = this.cloneState();
    raceStatus.sectors = (raceStatus.sectors ?? []).map((sector) => {
      const clearedSector = { ...sector };
      this.resetSector(clearedSector, raceStatus);
      return clearedSector;
    });
    raceStatus.incidents = (raceStatus.incidents ?? []).map((incident) => ({
      ...incident,
      sector: incident.sector ? this.clearedSectorCopy(incident.sector, raceStatus) : incident.sector,
      resolved: true,
    }));
    raceStatus.trackStatus = 'SAFE';
    this.replaceStateThenEvaluate(raceStatus);
  }

  protected evaluateRaceState(): void {
    this.stateMutationVersion++;
    this.loading = true;
    this.raceControlService.evaluateRaceState().subscribe({
      next: (decision) => {
        this.loading = false;
        this.currentDecision = decision;
        this.raceStatus = {
          ...(this.raceStatus ?? {}),
          currentDecision: decision,
        };
      },
      error: () => this.showError('Race state evaluation failed.'),
    });
  }

  protected applyRecommendation(): void {
    const decision = this.selectedDecision;
    if (!decision) {
      this.showError('There is no recommendation to apply.');
      return;
    }

    this.stateMutationVersion++;
    this.loading = true;
    this.raceControlService.applyDecision(decision).subscribe({
      next: (raceStatus) => {
        this.currentDecision = undefined;
        this.setRaceState(raceStatus);
      },
      error: () => this.showError('Recommendation could not be applied.'),
    });
  }

  protected clearRecommendation(): void {
    this.stateMutationVersion++;
    this.currentDecision = undefined;
    if (this.raceStatus) {
      this.raceStatus = { ...this.raceStatus, currentDecision: null };
    }
  }

  protected resetRace(): void {
    this.stateMutationVersion++;
    this.stopClock();
    this.loading = true;
    this.raceControlService.resetRaceState().subscribe({
      next: (raceStatus) => {
        this.currentDecision = undefined;
        this.setRaceState(raceStatus);
        this.startClock();
      },
      error: () => this.showError('Race state could not be reset.'),
    });
  }

  protected advanceClock(seconds: number, showLoading = true): void {
    if (!showLoading && this.clockAdvanceInFlight) {
      return;
    }

    if (this.loading && !showLoading) {
      this.advanceLocalClock(seconds);
      return;
    }

    if (showLoading) {
      this.stateMutationVersion++;
      this.loading = true;
    } else {
      this.clockAdvanceInFlight = true;
    }

    const requestVersion = this.stateMutationVersion;
    this.raceControlService.advanceClock(seconds).subscribe({
      next: (raceStatus) => {
        this.clockAdvanceInFlight = false;
        if (!showLoading && requestVersion !== this.stateMutationVersion) {
          return;
        }
        this.setRaceState(raceStatus, !showLoading);
      },
      error: () => {
        this.clockAdvanceInFlight = false;
        this.showError('Simulation clock could not be advanced.');
      },
    });
  }

  protected toggleClock(): void {
    if (this.clockRunning) {
      this.stopClock();
      return;
    }

    this.startClock();
  }

  protected evaluateDriver(): void {
    const driver = this.findDriver(this.violationForm.driverCode);
    if (!driver) {
      this.showError('Selected driver does not exist.');
      return;
    }

    const report = this.createDriverBehaviorReport(driver);
    const previousViolations = this.violationForm.repeated && !this.isTrackLimitsViolation
      ? (this.raceStatus?.violations ?? []).filter((violation) => violation.driver?.code === driver.code)
      : [];

    this.loading = true;
    this.raceControlService.evaluateDriverBehavior({ report, previousViolations }).subscribe({
      next: (raceStatus) => this.setRaceState(raceStatus),
      error: () => this.showError('Driver behavior evaluation failed.'),
    });
  }

  protected onViolationTypeChanged(type: string): void {
    if (type === 'TRACK_LIMITS') {
      this.violationForm.repeated = false;
    }
  }

  protected onIncidentTypeChanged(type: IncidentType): void {
    if (type === 'WEATHER') {
      this.incidentForm.blocked = false;
      this.incidentForm.marshals = false;
      this.incidentForm.medicalTeam = false;
      this.incidentForm.multipleSectors = true;
      this.incidentForm.driverLeftCar = false;
      return;
    }

    this.incidentForm.multipleSectors = false;
    if (type === 'DEBRIS' || type === 'FLUID_LEAK') {
      this.incidentForm.medicalTeam = false;
      this.incidentForm.driverLeftCar = false;
    }
  }

  protected startBlueFlagMonitoring(): void {
    const raceStatus = this.cloneState();
    const blueFlagDriver = this.findDriver(this.blueFlagForm.driverCode, raceStatus);
    if (!blueFlagDriver) {
      this.showError('Selected blue flag driver does not exist.');
      return;
    }

    const alreadyActive = (raceStatus.blueFlagMonitorings ?? []).some(
      (monitoring) => monitoring.driver?.code === blueFlagDriver.code && monitoring.active
    );
    if (alreadyActive) {
      this.showError('Blue flag monitoring is already active for selected driver.');
      return;
    }

    const now = raceStatus.simulationTime ?? new Date().toISOString();

    const previousBlueFlagMonitoring = (raceStatus.blueFlagMonitorings ?? []).find(
      (monitoring) => monitoring.driver?.code === blueFlagDriver?.code
    );

    raceStatus.blueFlagMonitorings = [
      ...(raceStatus.blueFlagMonitorings ?? []).filter(
        (monitoring) => monitoring.driver?.code !== blueFlagDriver.code
      ),
      this.createBlueFlagMonitoring(blueFlagDriver, now, previousBlueFlagMonitoring),
    ];

    this.startClock();
    this.saveStateThenEvaluateCep(raceStatus, 'Blue flag monitoring could not be started.');
  }

  protected markFasterCarPassed(): void {
    const raceStatus = this.cloneState();
    const monitoring = (raceStatus.blueFlagMonitorings ?? []).find(
      (item) => item.driver?.code === this.blueFlagForm.driverCode && item.active
    );

    if (!monitoring) {
      this.showError('There is no active blue flag monitoring for selected driver.');
      return;
    }

    monitoring.passedFasterCar = true;
    this.saveStateThenEvaluateCep(raceStatus, 'Blue flag pass confirmation could not be saved.');
  }

  protected evaluateBlueFlagCep(): void {
    this.evaluateCepOnly('Blue flag CEP evaluation failed.');
  }

  protected setVscDeltaStatus(driverCode: string | undefined, deltaStatus: 'RED' | 'GREEN'): void {
    if (!driverCode) {
      this.showError('Selected VSC delta driver does not exist.');
      return;
    }

    const raceStatus = this.cloneState();
    const monitoring = (raceStatus.vscDeltaMonitorings ?? []).find(
      (item) => item.driver?.code === driverCode && item.active
    );

    if (!monitoring) {
      this.showError('Start VSC delta monitoring before changing driver delta status.');
      return;
    }

    const now = raceStatus.simulationTime ?? new Date().toISOString();
    if (deltaStatus === 'RED') {
      if (monitoring.deltaStatus !== 'RED' || !monitoring.deltaRedSince) {
        monitoring.deltaRedSince = now;
      }
      monitoring.deltaStatus = 'RED';
    } else {
      monitoring.deltaStatus = 'GREEN';
      monitoring.deltaRedSince = null;
    }

    this.startClock();
    this.saveStateThenEvaluateCep(raceStatus, 'VSC delta status could not be saved.');
  }

  protected evaluateCep(): void {
    this.evaluateCepOnly('CEP evaluation failed.');
  }

  protected sectorClass(status: string): string {
    return `sector-${status.toLowerCase().replace('_', '-')}`;
  }

  protected flagIndicatorClass(status: string): string {
    return `flag-${status.toLowerCase().replace('_', '-')}`;
  }

  protected formatViolationLabel(value: string): string {
    return this.violationTypes.find((type) => type.value === value)?.label ?? value;
  }

  protected get violationUsesSector(): boolean {
    return this.violationForm.violationType !== 'UNSAFE_RELEASE'
      && this.violationForm.violationType !== 'PIT_SPEEDING';
  }

  protected get isTrackLimitsViolation(): boolean {
    return this.violationForm.violationType === 'TRACK_LIMITS';
  }

  protected get isSafetyCarViolation(): boolean {
    return this.violationForm.violationType === 'OVERTAKING_SAFETY_CAR'
      || this.violationForm.violationType === 'SAFETY_CAR_INFRINGEMENT';
  }

  protected get showSafetyCarHint(): boolean {
    return this.isSafetyCarViolation && this.safetyCarStatus !== 'SAFETY_CAR';
  }

  protected get isSingleYellowViolation(): boolean {
    return this.violationForm.violationType === 'OVERTAKING_YELLOW'
      || this.violationForm.violationType === 'DID_NOT_SLOW_YELLOW';
  }

  protected get isDoubleYellowViolation(): boolean {
    return this.violationForm.violationType === 'OVERTAKING_DOUBLE_YELLOW'
      || this.violationForm.violationType === 'DID_NOT_SLOW_DOUBLE_YELLOW';
  }

  protected get selectedViolationSectorFlag(): string {
    return this.raceStatus
      ? this.findSector(this.raceStatus, this.violationForm.sectorNumber)?.activeFlag ?? 'GREEN'
      : 'GREEN';
  }

  protected get showSingleYellowHint(): boolean {
    return this.isSingleYellowViolation && this.selectedViolationSectorFlag !== 'YELLOW';
  }

  protected get showDoubleYellowHint(): boolean {
    return this.isDoubleYellowViolation && this.selectedViolationSectorFlag !== 'DOUBLE_YELLOW';
  }

  protected get incidentUsesLocation(): boolean {
    return this.incidentForm.type !== 'WEATHER';
  }

  protected get incidentShowsMedicalTeam(): boolean {
    return this.incidentForm.type !== 'WEATHER'
      && this.incidentForm.type !== 'DEBRIS'
      && this.incidentForm.type !== 'FLUID_LEAK';
  }

  private setRaceState(raceStatus: RaceStatus, preservePendingDecision = false): void {
    const pendingDecision = preservePendingDecision && this.currentDecision && !this.currentDecision.applied
      ? this.currentDecision
      : undefined;

    this.loading = false;
    this.errorMessage = '';
    this.raceStatus = pendingDecision
      ? { ...raceStatus, currentDecision: pendingDecision }
      : raceStatus;
    this.currentDecision = pendingDecision ?? raceStatus.currentDecision ?? undefined;
    this.syncFormsWithState(raceStatus);
    this.syncVscDeltaMonitoringWithSession(raceStatus);
  }

  private replaceStateThenEvaluate(raceStatus: RaceStatus): void {
    this.stateMutationVersion++;
    this.loading = true;
    this.raceControlService.replaceRaceState(raceStatus).subscribe({
      next: (savedState) => {
        this.raceStatus = savedState;
        this.currentDecision = savedState.currentDecision ?? undefined;
        this.evaluateRaceState();
      },
      error: () => this.showError('Race facts could not be saved.'),
    });
  }

  private createDriverBehaviorReport(driver: Driver): DriverBehaviorReport {
    const type = this.violationForm.violationType;
    const safetyCarStatus: SafetyCarStatus =
      type === 'OVERTAKING_SAFETY_CAR' || type === 'SAFETY_CAR_INFRINGEMENT'
        ? 'SAFETY_CAR'
        : (this.safetyCarStatus as SafetyCarStatus);
    const activeFlag: FlagType =
      type === 'OVERTAKING_YELLOW' || type === 'DID_NOT_SLOW_YELLOW'
        ? 'YELLOW'
        : type === 'OVERTAKING_DOUBLE_YELLOW' || type === 'DID_NOT_SLOW_DOUBLE_YELLOW'
          ? 'DOUBLE_YELLOW'
        : (this.primarySectorFlag() as FlagType);

    return {
      driver,
      activeFlag,
      safetyCarStatus,
      didNotSlow: type === 'DID_NOT_SLOW_YELLOW'
        || type === 'DID_NOT_SLOW_DOUBLE_YELLOW'
        || type === 'SAFETY_CAR_INFRINGEMENT',
      overtook: type === 'OVERTAKING_YELLOW'
        || type === 'OVERTAKING_DOUBLE_YELLOW'
        || type === 'OVERTAKING_SAFETY_CAR',
      unsafeRelease: type === 'UNSAFE_RELEASE',
      pitSpeeding: type === 'PIT_SPEEDING',
      dangerousDriving: type === 'DANGEROUS_DRIVING',
      causingCollision: type === 'CAUSING_COLLISION',
      trackLimitsCount: type === 'TRACK_LIMITS' ? Number(this.violationForm.trackLimitsCount) : 0,
      time: this.raceStatus?.simulationTime ?? new Date().toISOString(),
      repeatedRequested: type !== 'TRACK_LIMITS' && this.violationForm.repeated,
      processed: false,
    };
  }

  private createBlueFlagMonitoring(
    driver: Driver,
    now: string,
    previous?: BlueFlagMonitoring
  ): BlueFlagMonitoring {
    return {
      driver,
      blueFlagShownAt: now,
      warningCount: previous?.active ? previous?.warningCount ?? 0 : 0,
      passedFasterCar: false,
      active: true,
    };
  }

  private createVscDeltaMonitoring(
    driver: Driver,
    now: string,
    deltaStatus: 'RED' | 'GREEN',
    previous?: VscDeltaMonitoring
  ): VscDeltaMonitoring {
    const isRed = deltaStatus === 'RED';
    const keepExistingStart = !!previous?.active && previous.driver?.code === driver.code;
    return {
      driver,
      vscActivatedAt: keepExistingStart ? previous?.vscActivatedAt ?? now : now,
      deltaStatus,
      deltaRedSince: isRed ? (keepExistingStart && previous?.deltaStatus === 'RED' ? previous?.deltaRedSince ?? now : now) : null,
      active: true,
    };
  }

  private syncFormsWithState(raceStatus: RaceStatus): void {
    const firstDriver = raceStatus.drivers?.[0];
    if (firstDriver && !this.findDriver(this.violationForm.driverCode, raceStatus)) {
      this.violationForm.driverCode = firstDriver.code ?? this.violationForm.driverCode;
    }

    if (firstDriver && !this.findDriver(this.blueFlagForm.driverCode, raceStatus)) {
      this.blueFlagForm.driverCode = firstDriver.code ?? this.blueFlagForm.driverCode;
    }

  }

  private cloneState(): RaceStatus {
    return structuredClone(this.raceStatus ?? {});
  }

  private findSector(raceStatus: RaceStatus, sectorNumber: number): TrackSector | undefined {
    return raceStatus.sectors?.find((sector) => sector.sectorNumber === Number(sectorNumber));
  }

  private findDriver(code: string, raceStatus = this.raceStatus): Driver | undefined {
    return raceStatus?.drivers?.find((driver) => driver.code === code);
  }

  private resetSector(sector: TrackSector, raceStatus?: RaceStatus): void {
    sector.blocked = false;
    sector.partiallyBlocked = false;
    sector.hasDebris = false;
    sector.hasStoppedVehicle = false;
    sector.marshalsOnTrack = false;
    sector.medicalTeamOnTrack = false;
    if (!raceStatus || this.canShowRecoveredSectorGreen(raceStatus)) {
      sector.activeFlag = 'GREEN';
    }
  }

  private clearedSectorCopy(sector: TrackSector, raceStatus?: RaceStatus): TrackSector {
    const clearedSector = { ...sector };
    this.resetSector(clearedSector, raceStatus);
    return clearedSector;
  }

  private applyRecoveryToSector(sector: TrackSector, raceStatus: RaceStatus): void {
    if (this.recoveryForm.blocked) {
      sector.blocked = false;
    }
    if (this.recoveryForm.debris) {
      sector.hasDebris = false;
    }
    if (this.recoveryForm.stoppedVehicle) {
      sector.hasStoppedVehicle = false;
    }
    if (this.recoveryForm.marshals) {
      sector.marshalsOnTrack = false;
    }
    if (this.recoveryForm.medicalTeam) {
      sector.medicalTeamOnTrack = false;
    }

    sector.partiallyBlocked = !sector.blocked && (
      !!sector.hasDebris
      || !!sector.hasStoppedVehicle
      || !!sector.marshalsOnTrack
      || !!sector.medicalTeamOnTrack
    );

    if (this.isSectorClear(sector) && this.canShowRecoveredSectorGreen(raceStatus)) {
      sector.activeFlag = 'GREEN';
    }
  }

  private canShowRecoveredSectorGreen(raceStatus: RaceStatus): boolean {
    return raceStatus.status === 'GREEN_FLAG'
      || raceStatus.currentDecision?.recommendedFlag === 'GREEN';
  }

  private isSectorClear(sector: TrackSector): boolean {
    return !sector.blocked
      && !sector.partiallyBlocked
      && !sector.hasDebris
      && !sector.hasStoppedVehicle
      && !sector.marshalsOnTrack
      && !sector.medicalTeamOnTrack;
  }

  private normalizeTrackStatus(raceStatus: RaceStatus): void {
    const sectors = raceStatus.sectors ?? [];
    if (sectors.some((sector) => sector.blocked)) {
      raceStatus.trackStatus = 'FULLY_BLOCKED';
      return;
    }
    if (sectors.some((sector) => !this.isSectorClear(sector))) {
      raceStatus.trackStatus = 'PARTIALLY_BLOCKED';
      return;
    }
    raceStatus.trackStatus = 'SAFE';
  }

  private sectorNote(sector: TrackSector): string {
    const notes: string[] = [];
    if (sector.blocked) notes.push('Blocked');
    if (sector.partiallyBlocked) notes.push('Partially blocked');
    if (sector.hasDebris) notes.push('Debris');
    if (sector.hasStoppedVehicle) notes.push('Stopped vehicle');
    if (sector.marshalsOnTrack) notes.push('Marshals');
    if (sector.medicalTeamOnTrack) notes.push('Medical team');
    return notes.length ? notes.join(', ') : 'Clear';
  }

  private primarySectorFlag(): string {
    const priority = ['RED', 'DOUBLE_YELLOW', 'YELLOW', 'BLUE', 'GREEN', 'NONE'];
    const flags = (this.raceStatus?.sectors ?? []).map((sector) => sector.activeFlag ?? 'NONE');
    return priority.find((flag) => flags.includes(flag as FlagType)) ?? 'NONE';
  }

  private penaltyLabel(penalty?: { type?: string; seconds?: number; reason?: string } | null): string {
    if (!penalty?.type) {
      return '-';
    }
    if (penalty.seconds && penalty.seconds > 0) {
      return `${penalty.seconds} sec`;
    }
    return this.formatEnumLabel(penalty.type);
  }

  private driverPenaltyLabel(driver: Driver): string {
    const penalties = (this.raceStatus?.violations ?? [])
      .filter((violation) => violation.driver?.code === driver.code && violation.penalty)
      .map((violation) => this.penaltyLabel(violation.penalty));

    if (penalties.length === 0 && driver.activePenalty) {
      penalties.push(this.penaltyLabel(driver.activePenalty));
    }

    const penaltyCounts = penalties
      .filter((penalty) => penalty !== '-')
      .reduce<Record<string, number>>((counts, penalty) => {
        counts[penalty] = (counts[penalty] ?? 0) + 1;
        return counts;
      }, {});

    const groupedPenalties = Object.entries(penaltyCounts)
      .map(([penalty, count]) => count > 1 ? `${penalty} x${count}` : penalty);

    return groupedPenalties.length ? groupedPenalties.join(' + ') : '-';
  }

  private activeBlueFlagWarnings(driverCode?: string): number {
    if (!driverCode) {
      return 0;
    }

    return this.activeBlueFlagMonitorings
      .filter((monitoring) => monitoring.driver?.code === driverCode)
      .reduce((total, monitoring) => total + (monitoring.warningCount ?? 0), 0);
  }

  protected vscDeltaElapsedSeconds(monitoring: VscDeltaMonitoring): number {
    if (monitoring.deltaStatus !== 'RED' || !monitoring.deltaRedSince || !this.raceStatus?.simulationTime) {
      return 0;
    }

    const startedAt = new Date(monitoring.deltaRedSince).getTime();
    const now = new Date(this.raceStatus.simulationTime).getTime();
    if (Number.isNaN(startedAt) || Number.isNaN(now) || now < startedAt) {
      return 0;
    }
    return Math.floor((now - startedAt) / 1000);
  }

  private formatEnumLabel(value?: string): string {
    if (!value || value === '-') {
      return '-';
    }
    if (value === 'VSC') {
      return 'VSC';
    }
    return value
      .toLowerCase()
      .split('_')
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  private shortTeam(team?: string): string {
    if (!team) return '-';
    return team
      .replace('Mercedes-AMG Petronas F1 Team', 'Mercedes')
      .replace('Scuderia Ferrari HP', 'Ferrari')
      .replace('McLaren Mastercard F1 Team', 'McLaren')
      .replace('Oracle Red Bull Racing', 'Red Bull')
      .replace('Atlassian Williams F1 Team', 'Williams')
      .replace('Aston Martin Aramco F1 Team', 'Aston Martin')
      .replace('BWT Alpine F1 Team', 'Alpine')
      .replace('Audi Revolut F1 Team', 'Audi')
      .replace('TGR Haas F1 Team', 'Haas')
      .replace('Cadillac Formula 1 Team', 'Cadillac');
  }

  private formatTime(value?: string): string {
    if (!value) {
      return '--:--:--';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value.substring(11, 19) || value;
    }
    return date.toLocaleTimeString('en-GB', { hour12: false });
  }

  private advanceLocalClock(seconds: number): void {
    if (!this.raceStatus?.simulationTime) {
      return;
    }

    const currentTime = new Date(this.raceStatus.simulationTime);
    if (Number.isNaN(currentTime.getTime())) {
      return;
    }

    this.raceStatus = {
      ...this.raceStatus,
      simulationTime: new Date(currentTime.getTime() + seconds * 1000).toISOString(),
    };
  }

  private showError(message: string): void {
    this.loading = false;
    this.errorMessage = message;
  }

  private evaluateCepOnly(errorMessage: string): void {
    this.stateMutationVersion++;
    this.loading = true;
    this.raceControlService.evaluateCep().subscribe({
      next: (updatedState) => this.setRaceState(updatedState),
      error: () => this.showError(errorMessage),
    });
  }

  private saveStateThenEvaluateCep(raceStatus: RaceStatus, errorMessage: string): void {
    this.stateMutationVersion++;
    this.loading = true;
    this.raceControlService.replaceRaceState(raceStatus).subscribe({
      next: () => this.evaluateCepOnly(errorMessage),
      error: () => this.showError(errorMessage),
    });
  }

  private syncVscDeltaMonitoringWithSession(raceStatus: RaceStatus): void {
    if (this.vscDeltaSyncInFlight) {
      return;
    }

    const monitorings = raceStatus.vscDeltaMonitorings ?? [];
    const hasActiveMonitoring = monitorings.some((monitoring) => monitoring.active);
    const shouldMonitor = raceStatus.status === 'VSC';

    if (shouldMonitor && !hasActiveMonitoring && raceStatus.drivers?.length) {
      const now = raceStatus.simulationTime ?? new Date().toISOString();
      const syncedState = structuredClone(raceStatus);
      syncedState.vscDeltaMonitorings = (syncedState.drivers ?? []).map((driver) =>
        this.createVscDeltaMonitoring(driver, now, 'GREEN')
      );
      this.startClock();
      this.persistVscDeltaSync(syncedState);
      return;
    }

    if (!shouldMonitor && hasActiveMonitoring) {
      const syncedState = structuredClone(raceStatus);
      syncedState.vscDeltaMonitorings = (syncedState.vscDeltaMonitorings ?? []).map((monitoring) => ({
        ...monitoring,
        deltaStatus: 'GREEN',
        deltaRedSince: null,
        active: false,
      }));
      this.persistVscDeltaSync(syncedState);
    }
  }

  private persistVscDeltaSync(raceStatus: RaceStatus): void {
    this.vscDeltaSyncInFlight = true;
    this.raceControlService.replaceRaceState(raceStatus).subscribe({
      next: (savedState) => {
        this.vscDeltaSyncInFlight = false;
        this.raceStatus = savedState;
        this.currentDecision = savedState.currentDecision ?? undefined;
      },
      error: () => {
        this.vscDeltaSyncInFlight = false;
        this.showError('VSC delta monitoring could not be synchronized.');
      },
    });
  }

  private startClock(): void {
    if (this.clockRunning) {
      return;
    }

    this.clockRunning = true;
    this.clockTimer = setInterval(() => this.advanceClock(1, false), 1000);
  }

  private stopClock(): void {
    if (this.clockTimer) {
      clearInterval(this.clockTimer);
      this.clockTimer = undefined;
    }
    this.clockRunning = false;
  }
}
