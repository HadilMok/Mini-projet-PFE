export interface dispoEntry {
  [date: string]: boolean[];
}
export interface disponibility {
  map: any;
  [id: number]: dispoEntry;
}
