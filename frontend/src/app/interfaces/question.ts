export enum RequestType {
  GRID = 'GRID',
  CODE = 'CODE',
  TEXT = 'TEXT'
}

export interface QuestionOptionRequest {
  text: string;
  isCorrect: boolean;
}

export interface QuestionTestCaseRequest {
  input: string;
  expectedOutput: string;
}

export interface QuestionRequest {
  text: string;
  points: number;
  type: RequestType;
  questionOptionRequest: QuestionOptionRequest[];
  questionTestCaseRequest: QuestionTestCaseRequest[];
}

export interface QuestionOption {
  id: number;
  text: string;
}

export interface QuestionTestCase {
  id: number;
  input: string;
  expectedOutput: string;
}

export interface Question {
  id: number;
  text: string;
  points: number;
  type: RequestType;
  options: QuestionOption[];
  testCases?: QuestionTestCase[];
}
