import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { User } from 'src/app/models/User';
import { BackendService } from 'src/app/services/backend.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent {
  
  username: string = '';
  oldPassword: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = ''; 
  loggedUser: User = new User();

  constructor(
    private backendService: BackendService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loggedUser = JSON.parse(localStorage.getItem("user") || "{}");
  }

  async onChangePassword() {
    this.errorMessage = ''; // resetuj poruku pri svakom pokušaju

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Nova lozinka i potvrda se ne poklapaju!';
      return;
    }

    try {
      const user = await firstValueFrom(this.backendService.login(this.username, this.oldPassword)) as User;
      await firstValueFrom(
        this.backendService.changePassword(user.userId || 0, this.newPassword)
      );

      this.router.navigate(['/login']);
    } catch (error) {
      this.errorMessage = 'Došlo je do greške pri promeni lozinke. Pokušajte ponovo.';
    }
  }
}
