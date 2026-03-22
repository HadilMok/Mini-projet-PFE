import React, { useEffect, useRef, useState } from "react";
import styles from "./disponibility.module.css";
import { object } from "zod";
import BlockElement from "@/components/specific/blockElement/BlockElement";
import { blockType } from "@/types/ComponentProps.Types";
import { Button } from "@/components/ui/button";
import { disponibility } from "@/types/ObjectTypes";
import { getPureDate } from "@/utils/utils";
export default function Disponibility({
  data: data,
  setData: setData,
}: {
  data: disponibility;
  setData: any;
}) {
  const [currentIds, setCurrentIds] = useState<string[]>([]);
  const [currentProfessor, setCurrentProfessor] = useState<number>(
    parseInt(Object.keys(data)[0])
  );
  const localProfList = localStorage.getItem("professorData")
    ? JSON.parse(localStorage.getItem("professorData"))
    : [];
  const [professorsList, setProfessorsList] = useState<any[]>(localProfList);
  const selection = useRef<HTMLSelectElement>(null);
  useEffect(() => {
    console.log("cuureent");
    console.log(data);
    setCurrentIds(Object.keys(data));
  }, []);
  useEffect(() => {
    console.log(data);
    console.log(currentProfessor);
    console.log(currentIds);
    setCurrentIds(Object.keys(data));
    //setCurrentProfessor(parseInt(Object.keys(data)[0]));
  }, [data]);
  function handleSelection(e: any) {
    if (selection.current) {
      setCurrentProfessor(parseInt(selection.current.value));
    }
  }
  function getProfName(num: number) {
    return (
      professorsList.find((prof: any) => prof.id == num).firstName +
      " " +
      professorsList.find((prof: any) => prof.id == num).lastName
    );
  }
  function handleData(e: any) {
    if (e.target) {
      const fullvalue = e.target.value;
      const seanceId = fullvalue.substring(0, fullvalue.indexOf(" ") + 1);
      let dateString = fullvalue.substring(fullvalue.indexOf(" ") + 1).trim();
      console.log(dateString);
      console.log(seanceId);
      console.log(data[currentProfessor][dateString][parseInt(seanceId)]);
      setData((prev: disponibility) => {
        let newChange = JSON.parse(JSON.stringify(prev));
        newChange[currentProfessor][dateString][parseInt(seanceId)] =
          !newChange[currentProfessor][dateString][parseInt(seanceId)];
        return newChange;
      });
    }
  }
  return (
    <div id={styles.container} className="">
      {/*
      {Object.entries(data).map(([id, value]) => (
        <div key={id}>
          {Object.entries(value).map(([key, val]) => (
            <p>
              {key}: {String(val)}
            </p>
          ))}
        </div>
      ))}*/}
      <select onChange={handleSelection} ref={selection}>
        {currentIds.map((key: string) => {
          return (
            <option value={key}>
              {"(" + key + ") " + getProfName(parseInt(key))}
            </option>
          );
        })}
      </select>
      <div className={styles.controlContainer}>
        {data[currentProfessor] &&
          Object.entries(data[currentProfessor]).map(([date, value]) => {
            return (
              <div className="spanner" id={styles.underContainer}>
                <span className={styles.date}>
                  {getPureDate(new Date(date))}
                </span>
                {value.map((e, index) => {
                  return (
                    <span className="spanner" id={styles.row}>
                      <button
                        className={e ? styles.true : styles.false}
                        onClick={(e: any) => handleData(e)}
                        value={index + " " + date}
                      >
                        S{index + 1}
                      </button>
                    </span>
                  );
                })}
              </div>
            );
          })}
      </div>
    </div>
  );
}
