import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Event } from 'src/app/models/Event';
import { EventOffer } from 'src/app/models/EventOffer';
import { User } from 'src/app/models/User';
import { BackendService } from 'src/app/services/backend.service';

@Component({
  selector: 'app-view-event-offer',
  templateUrl: './view-event-offer.component.html',
  styleUrls: ['./view-event-offer.component.css']
})
export class ViewEventOfferComponent {
  eventOffer: EventOffer | undefined;
  errorMsg: string = '';
  showEventList: Event[] = [];
  canComment: boolean = false;
  loggedInUser: User = new User();
  newGrade: number = 5;
  newComment: string = '';
  id: number = 0;
  eventsToComment: Event[] = [];
  eventDate!: Date; // yyyy-mm-dd format za input[type=date]
  numberOfPeople!: number;

  constructor(
    private route: ActivatedRoute,
    private backendService: BackendService
  ) {}

  async ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (isNaN(this.id)) {
      this.errorMsg = 'Nevažeći ID događaja';
      return;
    }

    try {
      this.eventOffer = await firstValueFrom(this.backendService.getOffer(this.id)) as EventOffer;
      this.showEventList = this.eventOffer.eventList?.filter(e =>
        e.eventStatusId?.eventStatusId === 2 && e.comment
      ) || [];

      this.loggedInUser = JSON.parse(localStorage.getItem("user") || "{}") as User;
      if (this.loggedInUser.userId)
        // Kada pripremaš listu događaja za komentarisanje:
        this.eventsToComment = this.eventOffer.eventList
        ?.filter(e => e.eventStatusId?.eventStatusId === 2 && 
                      e.customerId?.userId === this.loggedInUser?.userId &&
                      e.grade == null)
        .map(e => ({ ...e, grade: 0, hoverGrade: 0, newComment: '' })) || [];
        this.canComment = this.eventsToComment.length > 0;

    } catch (error: any) {
      this.errorMsg = 'Neuspešno učitavanje događaja';
    }
  }

  async submitComment(eventId: Event) {
    if (!eventId.eventId || !eventId.comment || !eventId.grade || !this.loggedInUser) return;
  
    try {
      // Example call to backend service
      await firstValueFrom(
        this.backendService.addComment(
          eventId.eventId,
          eventId.comment,
          eventId.grade
        )
      );
  
      // Refresh event list to include new comment
      this.eventOffer = await firstValueFrom(this.backendService.getOffer(this.id)) as EventOffer;
      this.showEventList = this.eventOffer.eventList?.filter(e =>
        e.eventStatusId?.eventStatusId === 2 && e.comment
      ) || [];
      // Kada pripremaš listu događaja za komentarisanje:
      this.eventsToComment = this.eventOffer.eventList
      ?.filter(e => e.eventStatusId?.eventStatusId === 2 && 
                    e.customerId?.userId === this.loggedInUser?.userId &&
                    e.grade == null)
      .map(e => ({ ...e, grade: 0, hoverGrade: 0, newComment: '' })) || [];

      this.canComment = this.eventsToComment.length > 0;
      this.newGrade = 5;
      this.newComment = '';
  
    } catch (error) {
      alert('Greška prilikom dodavanja komentara.');
    }
  }

  async addToCart() {
    if (!this.loggedInUser?.userId || !this.eventOffer?.eventOfferId) return;

    try {
      await firstValueFrom(
        this.backendService.addToCart(
          this.loggedInUser.userId,
          this.eventOffer.eventOfferId,
          this.numberOfPeople,
          this.eventDate // string yyyy-mm-dd, backend treba da parsira u Date
        )
      );
      alert('Događaj dodat u korpu!');
    } catch (error) {
      console.log(error)
      alert('Neuspešno dodavanje u korpu.');
    }
  }
}
