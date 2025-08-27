import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventOffersComponent } from './event-offers.component';

describe('EventOffersComponent', () => {
  let component: EventOffersComponent;
  let fixture: ComponentFixture<EventOffersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EventOffersComponent]
    });
    fixture = TestBed.createComponent(EventOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
