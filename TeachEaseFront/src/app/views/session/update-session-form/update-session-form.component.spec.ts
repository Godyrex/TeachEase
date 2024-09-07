import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSessionFormComponent } from './update-session-form.component';

describe('UpdateSessionFormComponent', () => {
  let component: UpdateSessionFormComponent;
  let fixture: ComponentFixture<UpdateSessionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateSessionFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateSessionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
