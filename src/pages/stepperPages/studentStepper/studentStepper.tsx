import { DataTable } from "@/components/common/data-table/data-table";
import FileUpload from "@/components/specific/fileUpload/fileUpload";
import styles from "./studentStepper.module.css";
import React from "react";
interface personStepperType {
  data: any[];
  setData: any;
}
export default function StudentStepper(props: personStepperType) {
  const setData = props.setData;
  const data = props.data;
  return (
    <div id={styles.container}>
      <h3 className={styles.title}>Students List</h3>
      <DataTable data={data} dataKey="studentData" setExternalData={setData} />
      <FileUpload setData={setData} dataKey="studentData" />
    </div>
  );
}
