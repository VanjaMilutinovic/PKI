import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { User } from 'src/app/models/User';
import { BackendService } from 'src/app/services/backend.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: User = new User();
  message: string = '';
  errorMessage: string = '';

  constructor(private backendService: BackendService, private router: Router) {}

  async ngOnInit() {
    try {
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        this.user = JSON.parse(storedUser);
      }
    } catch (error) {
      this.errorMessage = 'Neuspešno učitavanje korisnika.';
    }
  }

  async onUpdate() {
    try {
      const updatedUser = await firstValueFrom(
        this.backendService.updateUser(this.user.userId || 0, this.user)
      ) as User;
      this.user = updatedUser;
      localStorage.setItem('user', JSON.stringify(this.user));
      this.message = 'Podaci su uspešno izmenjeni.';
    } catch (error: any) {
      this.errorMessage = 'Greška pri izmeni podataka.';
    }
  }
}
