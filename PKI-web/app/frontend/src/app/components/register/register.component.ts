import { Component } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { BackendService } from 'src/app/services/backend.service';
import { User } from 'src/app/models/User';
import { UserType } from 'src/app/models/UserType';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  username: string = '';
  password: string = '';
  firstName: string = '';
  lastName: string = '';
  phone: string = '';
  address: string = '';
  errorMessage: string = '';

  constructor(
    private backend: BackendService,
    private router: Router
  ) {}

  async onRegister() {
    try {
      let costumer = new UserType();
      costumer.userTypeId = 2;
      const newUser: User = {
        userId: undefined,
        username: this.username,
        password: this.password,
        firstName: this.firstName,
        lastName: this.lastName,
        phone: this.phone,
        address: this.address,
        userTypeId: costumer
      };

      const savedUser = await firstValueFrom(this.backend.register(newUser)) as User;
      console.log('Registrovan korisnik:', savedUser);

      this.errorMessage = '';
      this.router.navigate(['/login']); // posle registracije vodi na login
    } catch (err) {
      console.error(err);
      this.errorMessage = 'Registracija nije uspela!';
    }
  }
}
