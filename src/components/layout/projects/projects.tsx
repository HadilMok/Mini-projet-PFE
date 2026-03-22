import { Button } from "@/components/ui/button";
import ProjectStepper from "@/pages/stepperPages/projectStepper/projectStepper";
import { Post } from "@/services/api";
import { projectType } from "@/types/ComponentProps.Types";
import React, { useState } from "react";
export default function Projects() {
  const professorsList = localStorage.getItem("professorData")
    ? JSON.parse(localStorage.getItem("professorData"))
    : [];
  const studentsList = localStorage.getItem("studentData")
    ? JSON.parse(localStorage.getItem("studentData"))
    : [];
  const [data, setData] = useState<projectType>(
    localStorage.getItem("projectData")
      ? JSON.parse(localStorage.getItem("projectData"))
      : []
  );
  const [status, setStatus] = useState<boolean>(false);
  const [statusg, setStatusg] = useState<boolean>(false);

  function getProfEmail(num: number) {
    return professorsList.find((prof: any) => prof.id == num).email;
  }
  function getStudentEmail(num: number) {
    return studentsList.find((prof: any) => prof.id == num).email;
  }
  function extract(s: string) {
    return parseInt(
      s.substr(s.indexOf("(") + 1, s.indexOf(")") - s.indexOf("(") - 1)
    );
  }
  async function save() {
    setStatus(true);
    for (let prof of data) {
      let newProject: any = {
        studentEmails: null,
        code: prof.id,
        note: prof.grade == "Not Graded Yet" ? null : prof.grade,
        name: prof.name,
        encadrantEmail: getProfEmail(prof.encadrant),
        rapporteurEmail: prof.rapporteur ? getProfEmail(prof.rapporteur) : null,
        presidentEmail: prof.president ? getProfEmail(prof.president) : null,
      };
      console.log(newProject);
      const studentsArray = [];

      if (prof.student1) {
        studentsArray.push(getStudentEmail(prof.student1));
      }
      if (prof.student2) {
        studentsArray.push(getStudentEmail(prof.student2));
      }
      newProject.studentEmails = studentsArray;
      console.log("newProject");
      console.log(newProject);
      const res = await Post("http://192.168.68.164:5555/projects", newProject);
      if (res.ok === 1) {
      } else {
        console.log(res.message);
      }
      console.log(res);
    }
    setStatus(false);
  }
  async function generate() {
    setStatusg(true);
    const res = await Post("link", { data: data });
    if (res.ok === 1) {
    } else {
      console.log(res.message);
    }
    setStatusg(false);
  }

  return (
    <>
      <ProjectStepper data={data} setData={setData} />
      <Button style={{ marginInline: "1em" }} disabled={status} onClick={save}>
        Save
      </Button>
      <Button disabled={statusg} onClick={generate}>
        Auto Generate
      </Button>
    </>
  );
}
