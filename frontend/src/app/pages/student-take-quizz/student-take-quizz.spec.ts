import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentTakeQuizz } from './student-take-quizz';

describe('StudentTakeQuizz', () => {
  let component: StudentTakeQuizz;
  let fixture: ComponentFixture<StudentTakeQuizz>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentTakeQuizz]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentTakeQuizz);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
