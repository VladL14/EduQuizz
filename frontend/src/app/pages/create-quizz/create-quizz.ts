import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { QuizService } from '../../services/quizz';
import { RequestType } from '../../interfaces/question';

@Component({
  selector: 'app-create-quizz',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-quizz.html',
  styleUrl: './create-quizz.css'
})
export class CreateQuizzComponent implements OnInit {
  quizForm: FormGroup;
  classroomId: number = 0;
  isSubmitting: boolean = false;
  RequestType = RequestType;
  questionTypes = [
    { value: RequestType.GRID, label: 'Grila' },
    { value: RequestType.CODE, label: 'Scriere Cod' },
    { value: RequestType.TEXT, label: 'Raspuns Text' }
  ];

  constructor(
    private fb: FormBuilder,
    private quizService: QuizService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.quizForm = this.fb.group({
      title: ['', Validators.required],
      timeLimit: [60, [Validators.required, Validators.min(1)]],
      activeFrom: ['', Validators.required],
      activeUntil: ['', Validators.required],
      questions: this.fb.array([])
    });
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.classroomId = Number(params['classId']);
    });
    this.addQuestion();
  }
  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  getOptions(questionIndex: number): FormArray {
    return this.questions.at(questionIndex).get('options') as FormArray;
  }

  getTestCases(questionIndex: number): FormArray {
    return this.questions.at(questionIndex).get('testCases') as FormArray;
  }

  addQuestion() {
    const questionGroup = this.fb.group({
      text: ['', Validators.required],
      points: [10, [Validators.required, Validators.min(1)]],
      type: [RequestType.GRID],
      options: this.fb.array([]),
      testCases: this.fb.array([])
    });

    this.questions.push(questionGroup);
    this.addOption(this.questions.length - 1);
    this.addOption(this.questions.length - 1);
  }

  removeQuestion(index: number) {
    this.questions.removeAt(index);
  }

  addOption(questionIndex: number) {
    const optionGroup = this.fb.group({
      text: ['', Validators.required],
      isCorrect: [false]
    });
    this.getOptions(questionIndex).push(optionGroup);
  }

  removeOption(questionIndex: number, optionIndex: number) {
    this.getOptions(questionIndex).removeAt(optionIndex);
  }

  addTestCase(questionIndex: number) {
    const testCaseGroup = this.fb.group({
      input: [''],
      expectedOutput: ['', Validators.required]
    });
    this.getTestCases(questionIndex).push(testCaseGroup);
  }

  removeTestCase(questionIndex: number, testCaseIndex: number) {
    this.getTestCases(questionIndex).removeAt(testCaseIndex);
  }

  onTypeChange(questionIndex: number) {
    const question = this.questions.at(questionIndex);
    const type = question.get('type')?.value;
    const optionsArr = this.getOptions(questionIndex);
    const testCasesArr = this.getTestCases(questionIndex);

    optionsArr.clear();
    testCasesArr.clear();

    if (type === RequestType.GRID) {
      this.addOption(questionIndex);
      this.addOption(questionIndex);
    } else if (type === RequestType.CODE) {
      this.addTestCase(questionIndex);
    }
  }

  onSubmit() {
    if (this.quizForm.invalid) {
      alert("Te rugam, completeaza toate campurile");
      return;
    }

    this.isSubmitting = true;
    const formVal = this.quizForm.value;
    const quizData = {
      title: formVal.title,
      classroomId: this.classroomId,
      activeFrom: formVal.activeFrom.length === 16 ? formVal.activeFrom + ':00' : formVal.activeFrom,
      activeUntil: formVal.activeUntil.length === 16 ? formVal.activeUntil + ':00' : formVal.activeUntil,
      timeLimit: formVal.timeLimit,
      questionRequest: formVal.questions.map((q: any) => ({
        text: q.text,
        points: q.points,
        type: q.type,
        questionOptionRequest: q.type === RequestType.GRID ? q.options.map((o: any) => ({
          text: o.text,
          isCorrect: o.isCorrect
        })) : [],
        questionTestCaseRequest: q.type === RequestType.CODE ? q.testCases.map((tc: any) => ({
          input: tc.input,
          expectedOutput: tc.expectedOutput
        })) : []
      }))
    };
    this.quizService.createQuiz(quizData).subscribe({
      next: (res) => {
        alert("Quiz-ul a fost salvat cu succes!");
        this.router.navigate(['/teacher/class', this.classroomId]);
      },
      error: (err) => {
        console.error("Eroare la salvare:", err);
        const errorMsg = err.error || "A aparut o eroare la salvare.";
        alert("Eroare: " + errorMsg);
        this.isSubmitting = false;
      }
    });
  }
  
  goBack() {
    this.router.navigate(['/teacher/class', this.classroomId]);
  }
}