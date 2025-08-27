import { Component, OnInit } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { BackendService } from 'src/app/services/backend.service';
import { Event } from 'src/app/models/Event';
import { User } from 'src/app/models/User';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: Event[] = [];
  loggedInUser: User | null = null;
  errorMsg: string = '';

  constructor(private backendService: BackendService) {}

  async ngOnInit() {
    try {
      this.loggedInUser = JSON.parse(localStorage.getItem('user') || 'null');
      if (!this.loggedInUser) throw new Error('User not logged in');

      this.cart = await firstValueFrom(
        this.backendService.getCart(this.loggedInUser.userId!)
      ) as Event[];

    } catch (error: any) {
      this.errorMsg = error.message || 'Greška prilikom učitavanja korpe';
    }
  }

  async removeFromCart(eventId: number) {
    try {
      await firstValueFrom(this.backendService.removeFromCart(eventId));
      this.cart = this.cart.filter(e => e.eventId !== eventId);
    } catch (err) {
      this.errorMsg = 'Greška prilikom brisanja iz korpe';
    }
  }

  async confirmCart() {
    if (!this.loggedInUser?.userId) return;
  
    try {
      await firstValueFrom(
        this.backendService.confirmCart(this.loggedInUser.userId)
      );
      this.cart = []; // isprazni korpu
      alert("Korpa uspešno potvrđena!");
    } catch (error) {
      this.errorMsg = "Greška prilikom potvrđivanja korpe";
      console.error("Greška prilikom potvrđivanja korpe:" + error);
    }
  }
  
  
}
