export interface blockType {
  from: number;
  to: number;
  block: string;
  id: string;
}
export interface projectType {
  id: string;
  grade?: number;
  name: string;
  encadrant?: number;
  president?: number;
  raporteur?: number;
  student1?: number;
  Student2?: number;
}

export interface scheduleType {
  id: string;
  grade: number;
  name: string;
  encadrant: string;
  president: string;
  rapporteur: string;
  student1: string;
  student2?: string;
  date: string;
  classroom: string;
  session: string;
}
