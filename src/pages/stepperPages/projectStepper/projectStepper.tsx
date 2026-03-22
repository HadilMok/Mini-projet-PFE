import { DataTable } from "@/components/common/data-table/data-table";
import FileUpload from "@/components/specific/fileUpload/fileUpload";
import styles from "./projectStepper.module.css";
import React from "react";
import { DataProjectTable } from "@/components/common/data-table/data-project-table";
import { projectType } from "@/types/ComponentProps.Types";
import ProjectFileUpload from "@/components/specific/projectFileUpload/projectFileUpload";
interface personStepperType {
  data: projectType[];
  setData: any;
}
export default function ProjectStepper(props: personStepperType) {
  const setData = props.setData;
  const data = props.data;
  return (
    <div id={styles.container}>
      <h3 className={styles.title}>Projects List</h3>
      <DataProjectTable
        setExternalData={setData}
        data={data as any}
        dataKey="projectData"
      />
      <ProjectFileUpload setData={setData} dataKey="projectData" />
    </div>
  );
}
