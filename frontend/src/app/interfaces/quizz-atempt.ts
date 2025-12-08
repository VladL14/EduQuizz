export interface QuizAttempt {
  id: number;
  grade: number;
  status: number;
  startTime: string;
  quiz: {
    id: number;
    title: string;
  };
}