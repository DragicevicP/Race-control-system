import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AdvanceTimeRequest,
  DriverBehaviorEvaluationRequest,
  RaceControlDecision,
  RaceStatus,
} from '../models/race-control.models';

@Injectable({ providedIn: 'root' })
export class RaceControlService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private readonly http: HttpClient) {}

  getRaceState(): Observable<RaceStatus> {
    return this.http.get<RaceStatus>(`${this.apiUrl}/race/state`);
  }

  replaceRaceState(raceStatus: RaceStatus): Observable<RaceStatus> {
    return this.http.put<RaceStatus>(`${this.apiUrl}/race/state`, raceStatus);
  }

  resetRaceState(): Observable<RaceStatus> {
    return this.http.post<RaceStatus>(`${this.apiUrl}/race/reset`, {});
  }

  evaluateRaceState(): Observable<RaceControlDecision> {
    return this.http.post<RaceControlDecision>(`${this.apiUrl}/race/evaluate`, {});
  }

  applyDecision(decision: RaceControlDecision): Observable<RaceStatus> {
    return this.http.post<RaceStatus>(`${this.apiUrl}/race/apply-decision`, decision);
  }

  advanceClock(seconds: number): Observable<RaceStatus> {
    const request: AdvanceTimeRequest = { seconds };
    return this.http.post<RaceStatus>(`${this.apiUrl}/race/clock/advance`, request);
  }

  evaluateDriverBehavior(request: DriverBehaviorEvaluationRequest): Observable<RaceStatus> {
    return this.http.post<RaceStatus>(`${this.apiUrl}/race/driver-behavior`, request);
  }

  evaluateCep(): Observable<RaceStatus> {
    return this.http.post<RaceStatus>(`${this.apiUrl}/race/cep/evaluate`, {});
  }
}
