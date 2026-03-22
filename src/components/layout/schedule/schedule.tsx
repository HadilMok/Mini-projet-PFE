import { DataProjectResultTable } from "@/components/common/data-table/data-project-result-table";
import { Button } from "@/components/ui/button";
import { Get, Post } from "@/services/api";
import { scheduleType } from "@/types/ComponentProps.Types";
import { useEffect, useState } from "react";

export default function Schedule() {
  const [status, setStatus] = useState<boolean>(false);
  const [generate, setGenerate] = useState<boolean>(false);
  const [data, setData] = useState<scheduleType[]>([]);
  async function sendReq() {
    setStatus(true);
    const res: any[] = await Get("http://192.168.68.164:5555/timetable", {});

    setData((prev: scheduleType[]) => {
      let results: scheduleType[] = [];
      for (let result of res.data) {
        console.log(result);
        results.push({
          id: result.id,
          name: result.name,
          grade: parseFloat(parseFloat(result.note).toFixed(2)),
          date: result.date.toString(),
          classroom: result.room.blocName + result.room.number,
          session: result.seance,
          encadrant:
            result.encadrant.firstName + " " + result.encadrant.lastName,
          president:
            result.president.firstName + " " + result.president.lastName,
          rapporteur:
            result.rapporteur.firstName + " " + result.rapporteur.lastName,
          student1:
            result.studentOne.firstName + " " + result.studentOne.lastName,
          student2: result.studentTwo
            ? result.studentTwo.firstName + " " + result.studentTwo.lastName
            : undefined,
        });
      }
      return results;
    });
    setGenerate(true);

    console.log(res);

    setStatus(false);
  }
  useEffect(() => {
    console.log(data);
  }, [data]);
  return (
    <div
      style={{
        width: "100%",
        height: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
      }}
    >
      <Button disabled={status} onClick={sendReq}>
        Generate Schedules
      </Button>{" "}
      {generate && (
        <DataProjectResultTable
          data={data}
          dataKey="ScheduleData"
          setExternalData={setData}
        />
      )}
    </div>
  );
}
