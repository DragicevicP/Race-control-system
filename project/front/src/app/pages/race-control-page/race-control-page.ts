import { Component } from '@angular/core';

@Component({
  selector: 'app-race-control-page',
  templateUrl: './race-control-page.html',
  styleUrl: './race-control-page.css',
})
export class RaceControlPage {
  protected readonly sectors = [
    { id: 1, name: 'Sector 1', status: 'GREEN', note: 'Rettifilo and Curva Grande clear' },
    { id: 2, name: 'Sector 2', status: 'DOUBLE_YELLOW', note: 'Debris near Lesmo' },
    { id: 3, name: 'Sector 3', status: 'VSC', note: 'Marshals near Ascari' },
  ];

  protected readonly drivers = [
    { position: 1, code: 'VER', team: 'Red Bull', gap: 'Leader', warnings: 0, penalty: '-' },
    { position: 2, code: 'LEC', team: 'Ferrari', gap: '+4.2', warnings: 1, penalty: '5 sec' },
    { position: 3, code: 'NOR', team: 'McLaren', gap: '+8.9', warnings: 0, penalty: '-' },
    { position: 14, code: 'ALB', team: 'Williams', gap: '+1 lap', warnings: 0, penalty: '-' },
  ];

  protected readonly events = [
    '00:12:04 Incident submitted in Sector 2',
    '00:12:04 Medium danger detected',
    '00:12:05 Double yellow recommended',
    '00:12:15 VSC delta monitoring active',
  ];

  protected sectorClass(status: string): string {
    return `sector-${status.toLowerCase().replace('_', '-')}`;
  }
}
