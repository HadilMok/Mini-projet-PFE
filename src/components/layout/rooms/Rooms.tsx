import { Button } from "@/components/ui/button";
import BlockStepper from "@/pages/stepperPages/blockStepper/blockStepper";
import { Delete, Post } from "@/services/api";
import { blockType } from "@/types/ComponentProps.Types";
import React, { useState } from "react";

export default function Rooms() {
  const [data, setData] = useState<blockType>(
    localStorage.getItem("blocks")
      ? JSON.parse(localStorage.getItem("blocks"))
      : []
  );

  const [status, setStatus] = useState<boolean>(false);

  async function save() {
    setStatus(true);
    let arr = [];
    await Delete("http://192.168.68.164:5555/classrooms/all", {});
    for (let prof of data) {
      for (
        let classnumber = parseInt(prof.from);
        classnumber < parseInt(prof.to) + 1;
        classnumber++
      ) {
        const obj = { blocName: prof.block, number: classnumber };

        const res = await Post("http://192.168.68.164:5555/classrooms", obj);
        if (res.ok === 1) {
        } else {
          console.log(res.message);
        }
        console.log(res);
      }
    }
    setStatus(false);
  }
  return (
    <>
      <BlockStepper data={data} setData={setData} />
      <Button disabled={status} onClick={save}>
        Save
      </Button>
    </>
  );
}
