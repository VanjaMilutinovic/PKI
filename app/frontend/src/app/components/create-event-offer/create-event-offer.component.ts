import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from 'src/app/services/backend.service';
import { EventOffer } from 'src/app/models/EventOffer';
import { Photo } from 'src/app/models/Photo';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-create-event-offer',
  templateUrl: './create-event-offer.component.html',
  styleUrls: ['./create-event-offer.component.css']
})
export class CreateEventOfferComponent {

  eventOffer: EventOffer = new EventOffer();
  selectedFileBase64?: string;
  errorMsg: string = '';

  constructor(private backendService: BackendService, private router: Router) {}

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedFileBase64 = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  async addEventOffer() {
    try {
      let photo: Photo = new Photo();
      if (this.selectedFileBase64) {
        photo = await firstValueFrom(this.backendService.addPhoto(this.selectedFileBase64)) as Photo;
      }
      this.eventOffer.photoId = photo;
      await firstValueFrom(this.backendService.addEventOffer(this.eventOffer)) as EventOffer;

      alert('Event offer successfully added!');
      this.router.navigate(['/event-offers']);
    } catch (error: any) {
      this.errorMsg = error.message || 'Error adding event offer.';
    }
  }
}
