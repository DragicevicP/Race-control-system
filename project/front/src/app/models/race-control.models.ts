export type FlagType = 'NONE' | 'GREEN' | 'YELLOW' | 'DOUBLE_YELLOW' | 'RED' | 'BLUE' | 'BLACK' | 'BLACK_ORANGE';
export type SafetyCarStatus = 'NONE' | 'VSC' | 'SAFETY_CAR';
export type RestartStatus = 'NONE' | 'RESTART_PROCEDURE' | 'RACE_RESTARTED';
export type SessionStatus = 'NOT_STARTED' | 'GREEN_FLAG' | 'VSC' | 'SAFETY_CAR' | 'RED_FLAG' | 'FINISHED';
export type TrackStatus = 'SAFE' | 'PARTIALLY_BLOCKED' | 'FULLY_BLOCKED';
export type IncidentType = 'STOPPED_VEHICLE' | 'CRASH' | 'DEBRIS' | 'WEATHER' | 'FLUID_LEAK';
export type TrackPosition = 'OFF_RACING_LINE' | 'TRACK_EDGE' | 'RACING_LINE';
export type DeltaStatus = 'RED' | 'GREEN';
export type PenaltyType = 'WARNING' | 'TIME_PENALTY' | 'DRIVE_THROUGH' | 'STOP_AND_GO' | 'BLACK_FLAG';
export type ViolationSeverity = 'VERY_LOW' | 'LOW' | 'MEDIUM' | 'HIGH' | 'VERY_HIGH' | 'EXTREME';

export interface Penalty {
  type?: PenaltyType;
  seconds?: number;
  reason?: string;
}

export interface Violation {
  code?: string;
  name?: string;
  description?: string;
  defaultSeverity?: ViolationSeverity;
  defaultPenaltyType?: PenaltyType;
}

export interface Driver {
  id?: number;
  code?: string;
  name?: string;
  team?: string;
  position?: number;
  gap?: string;
  violations?: DriverViolation[];
  warningCount?: number;
  activePenalty?: Penalty | null;
}

export interface DriverViolation {
  id?: number;
  driver?: Driver;
  violation?: Violation;
  severity?: ViolationSeverity;
  repeated?: boolean;
  penalty?: Penalty | null;
  time?: string;
}

export interface TrackSector {
  sectorNumber: number;
  blocked?: boolean;
  partiallyBlocked?: boolean;
  hasDebris?: boolean;
  hasStoppedVehicle?: boolean;
  marshalsOnTrack?: boolean;
  medicalTeamOnTrack?: boolean;
  activeFlag?: FlagType;
}

export interface Incident {
  id?: number;
  type?: IncidentType;
  sector?: TrackSector;
  position?: TrackPosition;
  multipleSectors?: boolean;
  driverLeftCar?: boolean;
  fluidLeak?: boolean;
  resolved?: boolean;
  dangerLevel?: string;
}

export interface RaceControlDecision {
  recommendedFlag?: FlagType;
  safetyCarStatus?: SafetyCarStatus;
  restartStatus?: RestartStatus;
  recommendedPenalty?: Penalty | null;
  explanation?: string;
  reasons?: string[];
  applied?: boolean;
  createdAt?: string;
}

export interface BlueFlagMonitoring {
  driver?: Driver;
  blueFlagShownAt?: string;
  warningCount?: number;
  passedFasterCar?: boolean;
  active?: boolean;
}

export interface VscDeltaMonitoring {
  driver?: Driver;
  vscActivatedAt?: string;
  deltaStatus?: DeltaStatus;
  deltaRedSince?: string | null;
  active?: boolean;
}

export interface RaceStatus {
  id?: number;
  status?: SessionStatus;
  trackStatus?: TrackStatus;
  sectors?: TrackSector[];
  incidents?: Incident[];
  drivers?: Driver[];
  violations?: DriverViolation[];
  blueFlagMonitorings?: BlueFlagMonitoring[];
  vscDeltaMonitorings?: VscDeltaMonitoring[];
  currentDecision?: RaceControlDecision | null;
  decisionLog?: RaceControlDecision[];
  simulationTime?: string;
}

export interface DriverBehaviorReport {
  driver?: Driver;
  activeFlag?: FlagType;
  safetyCarStatus?: SafetyCarStatus;
  didNotSlow?: boolean;
  overtook?: boolean;
  unsafeRelease?: boolean;
  pitSpeeding?: boolean;
  dangerousDriving?: boolean;
  causingCollision?: boolean;
  trackLimitsCount?: number;
  time?: string;
  processed?: boolean;
}

export interface DriverBehaviorEvaluationRequest {
  report: DriverBehaviorReport;
  previousViolations: DriverViolation[];
}

export interface AdvanceTimeRequest {
  seconds: number;
}
