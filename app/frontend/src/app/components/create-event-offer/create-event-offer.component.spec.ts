import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateEventOfferComponent } from './create-event-offer.component';

describe('CreateEventOfferComponent', () => {
  let component: CreateEventOfferComponent;
  let fixture: ComponentFixture<CreateEventOfferComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateEventOfferComponent]
    });
    fixture = TestBed.createComponent(CreateEventOfferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
