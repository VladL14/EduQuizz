import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentQuizz } from './student-quizz';

describe('StudentQuizz', () => {
  let component: StudentQuizz;
  let fixture: ComponentFixture<StudentQuizz>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentQuizz]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StudentQuizz);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
