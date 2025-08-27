import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import {Menu} from 'src/app/enums/Menu';
import { User } from 'src/app/models/User';
import { BackendService } from 'src/app/services/backend.service';
@Component({
  selector: 'app-header-and-menu',
  templateUrl: './header-and-menu.component.html',
  styleUrls: ['./header-and-menu.component.css']
})
export class HeaderAndMenuComponent implements OnInit{
  navItems = Menu;
  
  constructor(
    private router: Router, 
    private service: BackendService, 
  ) {}

  currentThemeDark = false;
  user: User = new User();
  // userJava: UserJava = new UserJava();
  logged: boolean = false;

  async ngOnInit() {
    try {
      this.user = JSON.parse(localStorage.getItem("user") || "{}") as User;
      if (this.user.userId) {
        this.logged = true;
      }
      console.warn(this.logged)

    } catch (error) {
      console.error("Error in header", error);
    }
  }

  toggleTheme() {
    const htmlElement = document.documentElement;
    if (htmlElement.getAttribute('data-theme') === 'dark') {
        htmlElement.removeAttribute('data-theme');
        this.currentThemeDark = false;
    } else {
        htmlElement.setAttribute('data-theme', 'dark');
        this.currentThemeDark = true;
    }
  }

  get filteredNavItems() {
    return this.navItems.filter(item => item.visible(this.user, this.logged));
  }

  async logout() {
    try {
      localStorage.removeItem("user");
      this.logged = false;
      this.router.navigate(['']);
    } catch (error: any) {
      console.error("Error while logging out:", error);
    }
  }
  
}
