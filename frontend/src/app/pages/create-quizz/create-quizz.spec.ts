import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateQuizz } from './create-quizz';

describe('CreateQuizz', () => {
  let component: CreateQuizz;
  let fixture: ComponentFixture<CreateQuizz>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateQuizz]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateQuizz);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
