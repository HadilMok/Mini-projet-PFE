export function getDaysArray(date1: Date, date2: Date): Date[] {
  let result: Date[] = [];
  let pureDate1 = new Date(
    `${date1.getFullYear()}-${date1.getMonth() + 1}-${date1.getDate()}`
  ).getTime();
  const pureDate2 = new Date(
    `${date2.getFullYear()}-${date2.getMonth() + 1}-${date2.getDate()}`
  ).getTime();
  while (pureDate1 <= pureDate2) {
    result.push(new Date(pureDate1));
    pureDate1 += 86400000;
  }
  return result;
}

export function getPureDate(date: Date): string {
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}
//export default { getPureDate, getDaysArray };
