import React, { useEffect, useState } from "react";
import styles from "./blockElement.module.css";
import { blockType } from "@/types/ComponentProps.Types";
import { Button } from "@/components/ui/button";

export default function BlockElement({
  id: id,
  data: data,
  setData: setData,
}: {
  id: string;
  data: blockType;
  setData: any;
}) {
  const [block, setBlock] = useState<string>(data.block);
  const [from, setFrom] = useState<number>(data.from);
  const [to, setTo] = useState<number>(data.to);
  function handleBlock(e: any) {
    setBlock(e.target.value);
  }
  function handleFrom(e: any) {
    setFrom(e.target.value);
  }
  function handleTo(e: any) {
    setTo(e.target.value);
  }
  function remove() {
    setData((prev: blockType[]) => {
      const res = prev.filter((element) => {
        return element.id != id;
      });
      //save statically
      localStorage.setItem("blocks", JSON.stringify(res));
      return res;
    });
  }
  useEffect(() => {
    setData((prev: blockType[]) => {
      let search = prev.findIndex((element: blockType) => element.id == id);

      return [
        ...prev.slice(0, search),
        { id: id, block: block, from: from, to: to },
        ...prev.slice(search + 1),
      ];
    });
  }, [to, from, block]);
  return (
    <div className="spanner">
      <div className="spanner" id={styles.aligner}>
        <h3>Block</h3>

        <input
          value={block}
          onChange={handleBlock}
          className={styles.block}
          type="text"
          placeholder="I"
        />
      </div>
      <div className="spanner" id={styles.aligner}>
        <div className="column">
          <h4>from</h4>
          <input
            value={from}
            onChange={handleFrom}
            className={styles.input}
            type="number"
            min={1}
            placeholder="1"
          />
        </div>
        <div className="column">
          <h4>to</h4>
          <input
            value={to}
            onChange={handleTo}
            className={styles.input}
            type="number"
            min={1}
            placeholder="1"
          />
        </div>
        <Button onClick={remove}>X</Button>
      </div>
    </div>
  );
}
