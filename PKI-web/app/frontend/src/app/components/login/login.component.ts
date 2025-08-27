import { Component } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { BackendService } from 'src/app/services/backend.service';
import { User } from 'src/app/models/User';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private backend: BackendService,
    private router: Router
  ) {}

  async onLogin() {
    try {
      const user = await firstValueFrom(this.backend.login(this.username, this.password)) as User;

      console.log('Ulogovan korisnik:', user);
      this.errorMessage = '';
      localStorage.setItem('user', JSON.stringify(user));

      // redirect posle logina
      this.router.navigate(['/home']);
    } catch (err) {
      console.error(err);
      this.errorMessage = 'Pogrešno korisničko ime ili lozinka!';
    }
  }
}
