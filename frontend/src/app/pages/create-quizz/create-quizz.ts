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

  addQuestion() {
    const questionGroup = this.fb.group({
      text: ['', Validators.required],
      points: [10, [Validators.required, Validators.min(1)]],
      type: [RequestType.GRID],
      options: this.fb.array([])
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
        questionOptionRequest: q.options.map((o: any) => ({
          text: o.text,
          isCorrect: o.isCorrect
        }))
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