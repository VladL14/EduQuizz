export enum RequestType {
  GRID = 'GRID',
  CODE = 'CODE',
  TEXT = 'TEXT'
}

export interface QuestionOptionRequest {
  text: string;
  isCorrect: boolean;
}

export interface QuestionRequest {
  text: string;
  points: number;
  type: RequestType;
  questionOptionRequest: QuestionOptionRequest[];
}