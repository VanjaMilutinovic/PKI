import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Promotion } from 'src/app/models/Promotion';
import { User } from 'src/app/models/User';
import { BackendService } from 'src/app/services/backend.service';

@Component({
  selector: 'app-promotions',
  templateUrl: './promotions.component.html',
  styleUrls: ['./promotions.component.css']
})
export class PromotionsComponent {
  allPromotions: Promotion[] = [];
  viewPromotions: Promotion[] = [];
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
      this.allPromotions = await firstValueFrom(this.backendService.getPromotions()) as Promotion[];
      this.shortenText();

      if (this.allPromotions.length > 0) {
        this.updateViewPromotions();
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

  open(p: Promotion) {
    this.router.navigate(['/view-promotion', p.promotionId]);
  }

  private shortenText() {
    const textarea = document.createElement('textarea');

    for (let i = 0; i < this.allPromotions.length; i++) {
      textarea.innerHTML = this.allPromotions[i].description.replace(/<[^>]*>/g, '');
      const text = textarea.value;
      this.allPromotions[i].description = text.length > 150
        ? text.slice(0, 150).trim() + '...'
        : text;
    }
  }

  private updateViewPromotions() {
    this.viewPromotions = [];
    const total = this.allPromotions.length;

    for (let i = 0; i < 3; i++) {
      const index = (this.currentIndex + i) % total;
      this.viewPromotions.push(this.allPromotions[index]);
    }
  }

  private startRotation() {
    this.intervalId = setInterval(() => {
      this.currentIndex = (this.currentIndex + 1) % this.allPromotions.length;
      this.updateViewPromotions();
    }, 1000); // menjanje svakih 5 sekundi
  }
}
