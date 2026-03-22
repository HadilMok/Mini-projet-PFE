import { Button } from "@/components/ui/button";
import BlockStepper from "@/pages/stepperPages/blockStepper/blockStepper";
import Disponibility from "@/pages/stepperPages/disponibility/disponibility";
import { Post } from "@/services/api";
import { blockType } from "@/types/ComponentProps.Types";
import { disponibility } from "@/types/ObjectTypes";
import React, { useState } from "react";

export default function Dispo() {
  const [data, setData] = useState<disponibility>(
    localStorage.getItem("disponibility")
      ? JSON.parse(localStorage.getItem("disponibility"))
      : []
  );

  const [status, setStatus] = useState<boolean>(false);

  async function save() {
    setStatus(true);
    const res = await Post("link", { data: data });
    if (res.ok === 1) {
    } else {
      console.log(res.message);
    }
    setStatus(false);
  }
  return (
    <>
      <Disponibility data={data} setData={setData} />
      <Button disabled={status} onClick={save}>
        Save
      </Button>
    </>
  );
}
