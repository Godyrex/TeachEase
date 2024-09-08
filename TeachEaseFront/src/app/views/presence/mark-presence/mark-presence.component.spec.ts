import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MarkPresenceComponent } from './mark-presence.component';

describe('MarkPresenceComponent', () => {
  let component: MarkPresenceComponent;
  let fixture: ComponentFixture<MarkPresenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MarkPresenceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MarkPresenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
