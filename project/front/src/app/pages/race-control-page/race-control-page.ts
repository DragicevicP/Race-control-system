import { Component, OnInit } from '@angular/core';
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
export class RaceControlPage implements OnInit {
  protected raceStatus?: RaceStatus;
  protected currentDecision?: RaceControlDecision;
  protected loading = false;
  protected errorMessage = '';

  protected incidentForm = {
    type: 'DEBRIS' as IncidentType,
    sectorNumber: 2,
    position: 'RACING_LINE' as TrackPosition,
    blocked: false,
    debris: true,
    marshals: true,
    medicalTeam: false,
    multipleSectors: false,
    driverLeftCar: false,
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
    active: true,
    passedFasterCar: false,
    warningCount: 1,
  };

  protected vscDeltaForm = {
    driverCode: 'LEC',
    deltaStatus: 'RED' as 'RED' | 'GREEN',
    active: true,
  };

  protected readonly incidentTypes = [
    { value: 'STOPPED_VEHICLE', label: 'Stopped vehicle' },
    { value: 'CRASH', label: 'Crash' },
    { value: 'DEBRIS', label: 'Debris' },
    { value: 'WEATHER', label: 'Weather' },
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
        warnings: driver.warningCount ?? 0,
        penalty: this.penaltyLabel(driver.activePenalty),
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

  protected get flagStatus(): string {
    return this.selectedDecision?.recommendedFlag ?? this.primarySectorFlag();
  }

  protected get safetyCarStatus(): string {
    return this.selectedDecision?.safetyCarStatus ?? 'NONE';
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
    return active?.driver?.code ? `${active.driver.code} active` : 'None active';
  }

  protected get vscDeltaMonitorLabel(): string {
    const active = (this.raceStatus?.vscDeltaMonitorings ?? []).find((monitoring) => monitoring.active);
    return active?.driver?.code ? `${active.driver.code} ${active.deltaStatus?.toLowerCase() ?? ''}` : 'None active';
  }

  protected loadRaceState(): void {
    this.loading = true;
    this.raceControlService.getRaceState().subscribe({
      next: (raceStatus) => this.setRaceState(raceStatus),
      error: () => this.showError('Backend state could not be loaded.'),
    });
  }

  protected evaluateSituation(): void {
    const raceStatus = this.cloneState();
    const sector = this.findSector(raceStatus, this.incidentForm.sectorNumber);
    if (!sector) {
      this.showError('Selected sector does not exist.');
      return;
    }

    sector.blocked = this.incidentForm.blocked;
    sector.partiallyBlocked = !this.incidentForm.blocked && (this.incidentForm.debris || this.incidentForm.marshals);
    sector.hasDebris = this.incidentForm.debris;
    sector.hasStoppedVehicle = this.incidentForm.type === 'STOPPED_VEHICLE';
    sector.marshalsOnTrack = this.incidentForm.marshals;
    sector.medicalTeamOnTrack = this.incidentForm.medicalTeam;

    const incident: Incident = {
      id: Date.now(),
      type: this.incidentForm.type,
      sector,
      position: this.incidentForm.position,
      multipleSectors: this.incidentForm.multipleSectors,
      driverLeftCar: this.incidentForm.driverLeftCar,
      fluidLeak: this.incidentForm.type === 'FLUID_LEAK',
      resolved: false,
    };

    raceStatus.incidents = [...(raceStatus.incidents ?? []), incident];
    this.replaceStateThenEvaluate(raceStatus);
  }

  protected evaluateRaceState(): void {
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
    this.currentDecision = undefined;
    if (this.raceStatus) {
      this.raceStatus = { ...this.raceStatus, currentDecision: null };
    }
  }

  protected resetRace(): void {
    this.loading = true;
    this.raceControlService.resetRaceState().subscribe({
      next: (raceStatus) => {
        this.currentDecision = undefined;
        this.setRaceState(raceStatus);
      },
      error: () => this.showError('Race state could not be reset.'),
    });
  }

  protected advanceClock(seconds: number): void {
    this.loading = true;
    this.raceControlService.advanceClock(seconds).subscribe({
      next: (raceStatus) => this.setRaceState(raceStatus),
      error: () => this.showError('Simulation clock could not be advanced.'),
    });
  }

  protected evaluateDriver(): void {
    const driver = this.findDriver(this.violationForm.driverCode);
    if (!driver) {
      this.showError('Selected driver does not exist.');
      return;
    }

    const report = this.createDriverBehaviorReport(driver);
    const previousViolations = this.violationForm.repeated
      ? (this.raceStatus?.violations ?? []).filter((violation) => violation.driver?.code === driver.code)
      : [];

    this.loading = true;
    this.raceControlService.evaluateDriverBehavior({ report, previousViolations }).subscribe({
      next: (raceStatus) => this.setRaceState(raceStatus),
      error: () => this.showError('Driver behavior evaluation failed.'),
    });
  }

  protected evaluateCep(): void {
    const raceStatus = this.cloneState();
    const blueFlagDriver = this.findDriver(this.blueFlagForm.driverCode, raceStatus);
    const vscDriver = this.findDriver(this.vscDeltaForm.driverCode, raceStatus);
    const now = raceStatus.simulationTime ?? new Date().toISOString();

    const previousBlueFlagMonitoring = (raceStatus.blueFlagMonitorings ?? []).find(
      (monitoring) => monitoring.driver?.code === blueFlagDriver?.code
    );
    const previousVscDeltaMonitoring = (raceStatus.vscDeltaMonitorings ?? []).find(
      (monitoring) => monitoring.driver?.code === vscDriver?.code
    );

    raceStatus.blueFlagMonitorings = blueFlagDriver
      ? [this.createBlueFlagMonitoring(blueFlagDriver, now, previousBlueFlagMonitoring)]
      : [];
    raceStatus.vscDeltaMonitorings = vscDriver
      ? [this.createVscDeltaMonitoring(vscDriver, now, previousVscDeltaMonitoring)]
      : [];

    this.loading = true;
    this.raceControlService.replaceRaceState(raceStatus).subscribe({
      next: () => {
        this.raceControlService.evaluateCep().subscribe({
          next: (updatedState) => this.setRaceState(updatedState),
          error: () => this.showError('CEP evaluation failed.'),
        });
      },
      error: () => this.showError('CEP facts could not be saved.'),
    });
  }

  protected sectorClass(status: string): string {
    return `sector-${status.toLowerCase().replace('_', '-')}`;
  }

  protected formatViolationLabel(value: string): string {
    return this.violationTypes.find((type) => type.value === value)?.label ?? value;
  }

  private setRaceState(raceStatus: RaceStatus): void {
    this.loading = false;
    this.errorMessage = '';
    this.raceStatus = raceStatus;
    this.currentDecision = raceStatus.currentDecision ?? undefined;
    this.syncFormsWithState(raceStatus);
  }

  private replaceStateThenEvaluate(raceStatus: RaceStatus): void {
    this.loading = true;
    this.raceControlService.replaceRaceState(raceStatus).subscribe({
      next: () => this.evaluateRaceState(),
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
        : (this.primarySectorFlag() as FlagType);

    return {
      driver,
      activeFlag,
      safetyCarStatus,
      didNotSlow: type === 'DID_NOT_SLOW_YELLOW' || type === 'SAFETY_CAR_INFRINGEMENT',
      overtook: type === 'OVERTAKING_YELLOW' || type === 'OVERTAKING_SAFETY_CAR',
      unsafeRelease: type === 'UNSAFE_RELEASE',
      pitSpeeding: type === 'PIT_SPEEDING',
      dangerousDriving: type === 'DANGEROUS_DRIVING',
      causingCollision: type === 'CAUSING_COLLISION',
      trackLimitsCount: type === 'TRACK_LIMITS' ? Number(this.violationForm.trackLimitsCount) : 0,
      time: this.raceStatus?.simulationTime ?? new Date().toISOString(),
      processed: false,
    };
  }

  private createBlueFlagMonitoring(
    driver: Driver,
    now: string,
    previous?: BlueFlagMonitoring
  ): BlueFlagMonitoring {
    const keepExistingStart = previous?.active && this.blueFlagForm.active && previous.driver?.code === driver.code;
    return {
      driver,
      blueFlagShownAt: keepExistingStart ? previous?.blueFlagShownAt ?? now : now,
      warningCount: Number(this.blueFlagForm.warningCount),
      passedFasterCar: this.blueFlagForm.passedFasterCar,
      active: this.blueFlagForm.active,
    };
  }

  private createVscDeltaMonitoring(
    driver: Driver,
    now: string,
    previous?: VscDeltaMonitoring
  ): VscDeltaMonitoring {
    const isRed = this.vscDeltaForm.deltaStatus === 'RED';
    return {
      driver,
      vscActivatedAt: previous?.vscActivatedAt ?? now,
      deltaStatus: this.vscDeltaForm.deltaStatus,
      deltaRedSince: isRed ? previous?.deltaRedSince ?? now : null,
      active: this.vscDeltaForm.active,
    };
  }

  private syncFormsWithState(raceStatus: RaceStatus): void {
    const firstDriver = raceStatus.drivers?.[0];
    if (firstDriver && !this.findDriver(this.violationForm.driverCode, raceStatus)) {
      this.violationForm.driverCode = firstDriver.code ?? this.violationForm.driverCode;
    }

    const activeBlueFlag = raceStatus.blueFlagMonitorings?.find((monitoring) => monitoring.active);
    if (activeBlueFlag?.driver?.code) {
      this.blueFlagForm.driverCode = activeBlueFlag.driver.code;
      this.blueFlagForm.active = !!activeBlueFlag.active;
      this.blueFlagForm.passedFasterCar = !!activeBlueFlag.passedFasterCar;
      this.blueFlagForm.warningCount = activeBlueFlag.warningCount ?? 0;
    }

    const activeDelta = raceStatus.vscDeltaMonitorings?.find((monitoring) => monitoring.active);
    if (activeDelta?.driver?.code) {
      this.vscDeltaForm.driverCode = activeDelta.driver.code;
      this.vscDeltaForm.deltaStatus = activeDelta.deltaStatus ?? 'GREEN';
      this.vscDeltaForm.active = !!activeDelta.active;
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
    return penalty.type.replace('_', ' ');
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

  private showError(message: string): void {
    this.loading = false;
    this.errorMessage = message;
  }
}
