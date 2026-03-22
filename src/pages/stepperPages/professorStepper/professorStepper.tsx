import { DataTable } from "@/components/common/data-table/data-table";
import FileUpload from "@/components/specific/fileUpload/fileUpload";
import styles from "./professerStepper.module.css";
import React from "react";
interface personStepperType {
  data: any[];
  setData: any;
  editable?: number;
}
export default function ProfessorStepper(props: personStepperType) {
  console.log(props.editable);
  const setData = props.setData;
  const data = props.data;
  const editable =
    props.editable == undefined ? true : props.editable == 1 ? true : false;
  return (
    <div id={styles.container}>
      <h3 className={styles.title}>Professors List</h3>
      <DataTable
        data={data}
        dataKey="professorData"
        setExternalData={setData}
      />
      {editable && <FileUpload setData={setData} dataKey="professorData" />}
    </div>
  );
}
