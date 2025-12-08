export interface QuizSummary {
  quizId: number;
  name: string;
  activeFrom: string;
  activeUntil: string;
  timeLimit: number;
}

export interface StudentGrade {
  studentId: number;
  username: string;
  email: string;
  grades: { [key: number]: number };
}

export interface ClassroomDashboard {
  classroomId: number;
  classroomName: string;
  code: string;
  quizzes: QuizSummary[];
  students: StudentGrade[];
}