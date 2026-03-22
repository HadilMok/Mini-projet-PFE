import React from "react";
import styles from "./popup.module.css";
import { Button } from "@/components/ui/button";
export default function Popup({
  setSendStatus: setSendStatus,
  message: message,
}: {
  setSendStatus: any;
  message?: string;
}) {
  return (
    <div id={styles.container}>
      <p style={{ marginBottom: "1em" }}>{message ?? ""}</p>
      <div className="spanner">
        <Button
          onClick={() => setSendStatus(1)}
          style={{ marginInline: "1em" }}
        >
          {" "}
          YES{" "}
        </Button>
        <Button
          onClick={() => setSendStatus(-1)}
          style={{ marginInline: "1em" }}
        >
          {" "}
          NO{" "}
        </Button>
      </div>
    </div>
  );
}
