import { RequestType, Question } from "./question";

export interface Quiz {
  id: number;
  title: string;
  activeFrom: string;
  activeUntil: string;
  timeLimit: number;
  questions: Question[];
 }

 export interface StudentResponse {
  questionId: number;
  selectedOptionIds: number[];
  textAnswer: string;
}

export interface SubmitQuizRequest {
  studentId: number;
  responses: StudentResponse[];
}

export interface CompilerRequest {
  code: string;
  input: string;
  expectedOutput: string;
}

export interface CompilerResponse {
  success: boolean;
  message: string;
}
