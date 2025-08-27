import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { EventOffersComponent } from './components/event-offers/event-offers.component';
import { PromotionsComponent } from './components/promotions/promotions.component';
import { ProfileComponent } from './components/profile/profile.component';
import { EventsComponent } from './components/events/events.component';
import { ViewEventOfferComponent } from './components/view-event-offer/view-event-offer.component';
import { CartComponent } from './components/cart/cart.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { AboutUsComponent } from './components/about-us/about-us.component';
import { SchedulingComponent } from './components/scheduling/scheduling.component';
import { CreateEventOfferComponent } from './components/create-event-offer/create-event-offer.component';


const routes: Routes = [
  { path: "about-us", component: AboutUsComponent },
  { path: "event-offers", component: EventOffersComponent },
  { path: "view-event-offer/:id", component: ViewEventOfferComponent },
  { path: "events", component: EventsComponent },
  { path: "promotions", component: PromotionsComponent },
  { path: "scheduling", component: SchedulingComponent },
  { path: "create-event-offer", component: CreateEventOfferComponent },
  { path: "profile", component: ProfileComponent },
  { path: "cart", component: CartComponent },
  { path: "notifications", component: NotificationsComponent },
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "change-password", component: ChangePasswordComponent },
  { path: "**", redirectTo: "event-offers" }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
