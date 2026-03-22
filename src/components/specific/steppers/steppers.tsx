import ProfessorStepper from "@/pages/stepperPages/professorStepper/professorStepper";
import React, { useContext, useEffect, useRef, useState } from "react";
import styles from "./steppers.module.css";
import { Button } from "@/components/ui/button";
import ProgressBar from "../progressBar/ProgressBar";
import StudentStepper from "@/pages/stepperPages/studentStepper/studentStepper";
import Popup from "@/components/common/popup/popup";
import { Post } from "@/services/api";
import { useNavigate } from "react-router";
import { StepperContext } from "@/context/StepperProvider";
import { PopupContext } from "@/context/PopupProvider";
import DateStepper from "@/pages/stepperPages/dateStepper/dateStepper";
import BlockStepper from "@/pages/stepperPages/blockStepper/blockStepper";
import { blockType, projectType } from "@/types/ComponentProps.Types";
import ProjectStepper from "@/pages/stepperPages/projectStepper/projectStepper";
import Disponibility from "@/pages/stepperPages/disponibility/disponibility";
import { dispoEntry, disponibility } from "@/types/ObjectTypes";
import { getDaysArray, getPureDate } from "../../../utils/utils";
export default function Steppers() {
  const localStudentData =
    JSON.parse(localStorage.getItem("studentData")) ?? [];
  const localProfessorData =
    JSON.parse(localStorage.getItem("professorData")) ?? [];

  const localFromDate =
    localStorage.getItem("fromDate") ?? JSON.stringify(new Date());
  const localToDate =
    localStorage.getItem("toDate") ?? JSON.stringify(new Date());
  const localBlocks = localStorage.getItem("blocks") ?? JSON.stringify([]);
  const localProjects =
    localStorage.getItem("projectData") ?? JSON.stringify([]);

  const localDisponibility = localStorage.getItem("disponibilty");

  const sliderRef = useRef<HTMLDivElement>(null);
  const [studentData, setStudentData] = useState<any[]>(localStudentData);
  const [professorData, setProfessorData] = useState<any[]>(localProfessorData);
  const [fromDate, setFromDate] = useState<Date>(
    localFromDate ? new Date(JSON.parse(localFromDate)) : new Date()
  );
  const [toDate, setToDate] = useState<Date>(
    localToDate ? new Date(JSON.parse(localToDate)) : new Date()
  );
  const [blocks, setBlocks] = useState<blockType[]>(JSON.parse(localBlocks));
  const [projects, setProjects] = useState<projectType[]>(
    JSON.parse(localProjects)
  );
  const [dispo, setDispo] = useState<disponibility>(
    localDisponibility ? JSON.parse(localDisponibility) : initialDispo()
  );

  const [pagination, setPagination] = useState<number>(1);

  const [finalize, setFinalize] = useState<boolean>(false);
  const [check, setCheck] = useState<boolean>(false);
  const [sendStatus, setSendStatus] = useState<number>(0);

  const Navigate = useNavigate();
  const StepperSettings = useContext(StepperContext);
  const PopupSettings = useContext(PopupContext);
  function initialDispo() {
    const datesArray: Date[] = getDaysArray(fromDate, toDate);
    //treat initialdispo

    let prev: disponibility = {};

    professorData.forEach((el: any) => {
      let dispose: dispoEntry = {};
      datesArray.forEach((e: Date) => {
        dispose[e.toString()] = [true, true, true, true, true, true];
      });
      const currentId = el.id;
      const newDatedata = JSON.parse(JSON.stringify(prev));
      prev[currentId] = dispose;
      return newDatedata;
    });
    return prev;
  }
  function checkDate() {
    return fromDate && toDate && fromDate <= toDate;
  }
  function saveDates() {
    localStorage.setItem("fromDate", JSON.stringify(fromDate));
    localStorage.setItem("toDate", JSON.stringify(toDate));
  }
  function saveDispo() {
    localStorage.setItem("disponibility", JSON.stringify(dispo));
  }
  function handlePrev() {
    setPagination((prev) => Math.max(1, prev - 1));
  }
  function checkBlocks() {
    let blockFreq: any = {};
    for (let block of blocks) {
      console.log(block.from);
      console.log(block.to);
      if (block.from > block.to) return false;
      if (blockFreq[block.block]) return false;
      else blockFreq[block.block] = 0;
      blockFreq[block.block]++;
    }
    return true;
  }

  function saveBlocks() {
    localStorage.setItem("blocks", JSON.stringify(blocks));
  }
  function handleNext() {
    if (pagination == 2 && !checkDate()) {
      //animate before decline
      if (sliderRef.current) {
        console.log("hi");
        sliderRef.current.style.transform = `translateX(${
          -95 * (pagination - 1)
        }%)`;
      }
      setTimeout(() => {
        if (sliderRef.current)
          sliderRef.current.style.transform = `translateX(${
            -100 * (pagination - 1)
          }%)`;
      }, 200);
      //decline
      return;
    }

    if (pagination == 1 && !checkBlocks()) {
      if (sliderRef.current)
        sliderRef.current.style.transform = "translateX(1em)";
      setTimeout(() => {
        if (sliderRef.current)
          sliderRef.current.style.transform = "translateX(0)";
      }, 200);
      return;
    }
    if (pagination == 1) {
      saveBlocks();
    }
    if (pagination == 2) {
      saveDates();
    }
    setPagination((prev) => Math.min(6, prev + 1));
    if (pagination == 6) {
      //handle done
      setFinalize(true);
    }
  }
  async function sendData() {
    const studentData = localStorage.getItem("studentData");
    const professorData = localStorage.getItem("professorData");

    //if (!studentData || !professorData) return;
    /*
    const ret = await Post("", {
      studentData: JSON.parse(studentData),
      professorData: JSON.parse(professorData),
    });*/
    const ret = { status: 1000, ok: 1 };
    console.log(ret);
    if (ret.ok == 1) {
      //all good
      //change context
      StepperSettings.setStepperPhase(true);

      //navigate to dashboard
      Navigate("/");
    } else {
      //notify error TODO
    }
  }
  /*  const [studentData, setStudentData] = useState<any[]>(localStudentData);
  const [professorData, setProfessorData] = useState<any[]>(localProfessorData);
  const [fromDate, setFromDate] = useState<Date>(
    localFromDate ? new Date(JSON.parse(localFromDate)) : new Date()
  );
  const [toDate, setToDate] = useState<Date>(
    localToDate ? new Date(JSON.parse(localToDate)) : new Date()
  );
  const [blocks, setBlocks] = useState<blockType[]>(JSON.parse(localBlocks));
  const [projects, setProjects] = useState<projectType[]>(
    JSON.parse(localProjects)
  );
  const [dispo, setDispo] = useState<disponibility>(
    localDisponibility ? JSON.parse(localDisponibility) : initialDispo()
  );
*/
  useEffect(() => {
    //if professors change
    setDispo(initialDispo());
    console.log("professorDatachange");
    console.log(professorData);
  }, [professorData]);
  useEffect(() => {
    //if date change
    setDispo(initialDispo());
    console.log("date data change");
    console.log(dispo);
  }, [fromDate, toDate]);
  useEffect(() => {
    console.log(dispo);
  }, [dispo]);

  useEffect(() => {
    if (finalize && !check) {
      //send data backend
      setCheck(true);
    }
  }, [finalize]);

  useEffect(() => {
    if (check) {
      PopupSettings.setPopup(true);
    } else {
      //turn it all off
      console.log("here");
      PopupSettings.setPopup(false);
      setCheck(false);
      setFinalize(false);
      setSendStatus(0);
    }
  }, [check]);

  useEffect(() => {
    if (sendStatus) {
      if (sendStatus == -1) {
        //not done yet
        setCheck(false);
      } else {
        //done with all
        setCheck(false);
        saveDispo();

        sendData();
      }
    }
  }, [sendStatus]);
  return (
    <div id={styles.container}>
      {check && (
        <Popup
          setSendStatus={setSendStatus}
          message="Confirm Finished Steppers Please"
        />
      )}
      <div className="spanner" id={styles.controller}>
        <Button onClick={handlePrev} disabled={pagination == 1}>
          Previous
        </Button>
        <ProgressBar page={pagination} />
        <Button onClick={handleNext} disabled={finalize}>
          {pagination < 6 ? "Next" : "Done"}
        </Button>
      </div>
      <div className="spanner" id={styles.pagingContainer}>
        <div
          ref={sliderRef}
          className="spanner"
          id={styles.paging}
          style={{ transform: `translateX(${-100 * (pagination - 1)}%)` }}
        >
          {/*stepper pages*/}
          <BlockStepper data={blocks} setData={setBlocks} />
          <DateStepper
            fromDate={fromDate}
            toDate={toDate}
            setToDate={setToDate}
            setFromDate={setFromDate}
          />
          <StudentStepper data={studentData} setData={setStudentData} />
          <ProfessorStepper data={professorData} setData={setProfessorData} />
          <ProjectStepper data={projects} setData={setProjects} />
          <Disponibility data={dispo} setData={setDispo} />
        </div>
      </div>
    </div>
  );
}
