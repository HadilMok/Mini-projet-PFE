import { Button } from "@/components/ui/button";
import DateStepper from "@/pages/stepperPages/dateStepper/dateStepper";
import ProfessorStepper from "@/pages/stepperPages/professorStepper/professorStepper";
import { Delete, Post } from "@/services/api";
import React, { useEffect, useState } from "react";

interface personStepperType {
  data: any[];
  setData: any;
}
export default function DateComp() {
  const fromDate = localStorage.getItem("fromDate")
    ? JSON.parse(localStorage.getItem("fromDate"))
    : [];
  const toDate = localStorage.getItem("toDate")
    ? JSON.parse(localStorage.getItem("toDate"))
    : [];
  const [fDate, setFdate] = useState<Date>(new Date(fromDate));
  const [tDate, settDate] = useState<Date>(new Date(toDate));
  const [status, setStatus] = useState<boolean>(false);
  async function save() {
    setStatus(true);
    await Delete("http://192.168.68.164:5555/configuration", {});
    const res = await Post("http://192.168.68.164:5555/configuration", {
      status: false,
      startDate: fDate.toISOString(),
      endDate: tDate.toISOString(),
    });
    if (res.ok === 1) {
    } else {
      console.log(res.message);
    }
    console.log(res);

    setStatus(false);
  }
  return (
    <>
      <DateStepper
        editable={0}
        setFromDate={setFdate}
        setToDate={settDate}
        fromDate={fDate}
        toDate={tDate}
      />
      <Button disabled={status} onClick={save}>
        Save
      </Button>
    </>
  );
}
