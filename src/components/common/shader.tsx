import React from "react";

export default function Shade() {
  return (
    <div
      style={{
        minWidth: "100%",
        height: "150%",
        left: "0%",
        top: "0%",
        backdropFilter: "blur(5px)",
        transform: "translate(0%,0%)",
        position: "absolute",
        zIndex: "999",
      }}
    ></div>
  );
}
