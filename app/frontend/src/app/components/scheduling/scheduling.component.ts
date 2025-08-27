import { Component, OnInit } from '@angular/core';
import { BackendService } from 'src/app/services/backend.service';
import { firstValueFrom } from 'rxjs';
import { Event } from 'src/app/models/Event';

@Component({
  selector: 'app-scheduling',
  templateUrl: './scheduling.component.html',
  styleUrls: ['./scheduling.component.css']
})
export class SchedulingComponent implements OnInit {
  events: Event[] = [];
  errorMsg: string = '';

  constructor(private backendService: BackendService) {}

  async ngOnInit() {
    try {
      // Preuzmi sve događaje u statusu 4 (čekaju potvrdu)
      this.events = await firstValueFrom(this.backendService.getEventsByStatus(4)) as Event[];
    } catch (error: any) {
      this.errorMsg = error.error || 'Greška pri učitavanju događaja';
    }
  }

  async acceptEvent(eventId: number) {
    try {
      await firstValueFrom(this.backendService.approveEvent(eventId));
      this.events = this.events.filter(e => e.eventId !== eventId);
    } catch (error: any) {
      console.error(error)
      alert('Greška pri prihvatanju događaja');
    }
  }

  async declineEvent(eventId: number) {
    try {
      await firstValueFrom(this.backendService.declineEvent(eventId));
      this.events = this.events.filter(e => e.eventId !== eventId);
    } catch (error: any) {
      console.error(error)
      alert('Greška pri odbijanju događaja');
    }
  }
}
