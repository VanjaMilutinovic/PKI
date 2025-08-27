import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewEventOfferComponent } from './view-event-offer.component';

describe('ViewEventOfferComponent', () => {
  let component: ViewEventOfferComponent;
  let fixture: ComponentFixture<ViewEventOfferComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewEventOfferComponent]
    });
    fixture = TestBed.createComponent(ViewEventOfferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
