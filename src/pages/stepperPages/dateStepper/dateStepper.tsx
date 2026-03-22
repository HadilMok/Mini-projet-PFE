import { DataTable } from "@/components/common/data-table/data-table";
import styles from "./dateStepper.module.css";
import React, { useEffect } from "react";
import { DatePicker } from "@/components/common/datePicker/datePicker";
import { getPureDate } from "@/utils/utils";
interface personStepperType {
  fromDate: Date | null;
  toDate: Date | null;
  setFromDate: any;
  setToDate: any;
  editable?: number;
}
export default function DateStepper(props: personStepperType) {
  const setFromDate = props.setFromDate;
  const setToDate = props.setToDate;
  const fromDate = props.fromDate;
  const toDate = props.toDate;
  const editable =
    props.editable == undefined ? true : props.editable == 1 ? true : false;
  return (
    <div id={styles.container}>
      <h1>Soutenances</h1>
      <div className="column">
        <div className="spanner" id={styles.date}>
          <h3>From</h3>
          {editable ? (
            <DatePicker setMainDate={setFromDate} date={fromDate} />
          ) : (
            <>
              <div className="spanner" id={styles.date}>
                <h3>From</h3>
                <h1>{getPureDate(new Date(fromDate))}</h1>
              </div>
            </>
          )}
        </div>
        <div className="spanner" id={styles.date}>
          <h3>To</h3>
          {editable ? (
            <DatePicker setMainDate={setToDate} date={toDate} />
          ) : (
            <div className="spanner" id={styles.date}>
              <h3>To</h3>
              <h1>{getPureDate(new Date(toDate))}</h1>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
