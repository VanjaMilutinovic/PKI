import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { EditorModule } from '@tinymce/tinymce-angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { FooterComponent } from './components/util/footer/footer.component';
import { HeaderAndMenuComponent } from './components/util/header-and-menu/header-and-menu.component';
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

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    HeaderAndMenuComponent,
    LoginComponent,
    RegisterComponent,
    ChangePasswordComponent,
    EventOffersComponent,
    PromotionsComponent,
    ProfileComponent,
    EventsComponent,
    ViewEventOfferComponent,
    CartComponent,
    NotificationsComponent,
    AboutUsComponent,
    SchedulingComponent,
    CreateEventOfferComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    EditorModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
