import { Button } from "@/components/ui/button";
import ProfessorStepper from "@/pages/stepperPages/professorStepper/professorStepper";
import StudentStepper from "@/pages/stepperPages/studentStepper/studentStepper";
import { Post } from "@/services/api";
import React, { useState } from "react";

interface personStepperType {
  data: any[];
  setData: any;
}
export default function Students() {
  const [data, setData] = useState<personStepperType>(
    localStorage.getItem("studentData")
      ? JSON.parse(localStorage.getItem("studentData"))
      : []
  );

  const [status, setStatus] = useState<boolean>(false);
  async function save() {
    setStatus(true);
    for (let prof of data) {
      const { id, ...clean } = prof;
      console.log(clean);
      const res = await Post("http://192.168.68.164:5555/students", clean);
      if (res.ok === 1) {
      } else {
        console.log(res.message);
      }
      console.log(res);
    }
    setStatus(false);
  }
  return (
    <>
      <StudentStepper data={data} setData={setData} />
      <Button disabled={status} onClick={save}>
        Save
      </Button>
    </>
  );
}
