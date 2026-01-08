export interface QuizSummary {
  id: number;
  title: string;
  activeFrom: string;
  activeUntil: string;
  timeLimit: number;
  status?: string;
  grade?: number;
}

export interface StudentGrade {
  studentId: number;
  username: string;
  email: string;
  grades: { [key: number]: string | number };
}

export interface ClassroomDashboard {
  classroomId: number;
  classroomName: string;
  code: string;
  quizzes: QuizSummary[];
  students: StudentGrade[];
}