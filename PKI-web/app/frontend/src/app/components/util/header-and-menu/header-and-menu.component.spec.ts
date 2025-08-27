import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderAndMenuComponent } from './header-and-menu.component';

describe('HeaderAndMenuComponent', () => {
  let component: HeaderAndMenuComponent;
  let fixture: ComponentFixture<HeaderAndMenuComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HeaderAndMenuComponent]
    });
    fixture = TestBed.createComponent(HeaderAndMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
