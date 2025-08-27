import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { EventOffer } from 'src/app/models/EventOffer';
import { BackendService } from 'src/app/services/backend.service';
import { firstValueFrom } from 'rxjs';
import { User } from 'src/app/models/User';

@Component({
  selector: 'app-event-offers',
  templateUrl: './event-offers.component.html',
  styleUrls: ['./event-offers.component.css']
})
export class EventOffersComponent implements OnDestroy {
  allEvents: EventOffer[] = [];
  viewEvents: EventOffer[] = [];
  errorMsg: string = '';
  user: User = new User();
  private currentIndex: number = 0;
  private intervalId: any;

  constructor(
    private backendService: BackendService, 
    private router: Router
  ) {}

  async ngOnInit() {
    try {
      this.user = JSON.parse(localStorage.getItem("user") || "");
    } catch (error: any) {}

    try {
      this.allEvents = await firstValueFrom(this.backendService.getOffers()) as EventOffer[];
      this.shortenText();

      if (this.allEvents.length > 0) {
        this.updateViewEvents();
        this.startRotation();
      }
    } catch (error: any) {
      this.errorMsg = error.error || "Došlo je do greške pri učitavanju ponuda.";
    }
  }

  ngOnDestroy() {
    // Zaustavi interval kada se komponenta uništi
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  open(e: EventOffer) {
    this.router.navigate(['/view-event-offer', e.eventOfferId]);
  }

  private shortenText() {
    const textarea = document.createElement('textarea');

    for (let i = 0; i < this.allEvents.length; i++) {
      textarea.innerHTML = this.allEvents[i].shortDescription.replace(/<[^>]*>/g, '');
      const text = textarea.value;
      this.allEvents[i].shortDescription = text.length > 150
        ? text.slice(0, 150).trim() + '...'
        : text;
    }
  }

  private updateViewEvents() {
    this.viewEvents = [];
    const total = this.allEvents.length;

    for (let i = 0; i < 3; i++) {
      const index = (this.currentIndex + i) % total;
      this.viewEvents.push(this.allEvents[index]);
    }
  }

  private startRotation() {
    this.intervalId = setInterval(() => {
      this.currentIndex = (this.currentIndex + 1) % this.allEvents.length;
      this.updateViewEvents();
    }, 5000); // menjanje svakih 5 sekundi
  }
}
