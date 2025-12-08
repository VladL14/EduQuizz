import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassDashboard } from './class-dashboard';

describe('ClassDashboard', () => {
  let component: ClassDashboard;
  let fixture: ComponentFixture<ClassDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClassDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
