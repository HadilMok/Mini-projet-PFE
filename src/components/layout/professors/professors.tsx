import { Button } from "@/components/ui/button";
import ProfessorStepper from "@/pages/stepperPages/professorStepper/professorStepper";
import { Post } from "@/services/api";
import React, { useEffect, useState } from "react";

interface personStepperType {
  data: any[];
  setData: any;
}
export default function Professors() {
  const localData = localStorage.getItem("professorData")
    ? JSON.parse(localStorage.getItem("professorData"))
    : [];
  const [data, setData] = useState<personStepperType>(localData);
  const [status, setStatus] = useState<boolean>(false);
  async function save() {
    setStatus(true);
    for (let prof of data) {
      const { id, ...clean } = prof;
      const res = await Post("http://192.168.68.164:5555/professors", clean);
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
      <ProfessorStepper editable={0} data={data} setData={setData} />
      <Button disabled={status} onClick={save}>
        Save
      </Button>
    </>
  );
}
