import { StepperContext, StepperProvider } from "@/context/StepperProvider";
import React, { useContext, useEffect, useState } from "react";
import { Routes } from "react-router";
import { Route } from "react-router-dom";
import home from "./home";
import Home from "./home";
import NotFound from "../fallback/notfound";
import Dashboard from "../dashboard/dashboard";

export default function HomeApp() {
  const StepperSettings = useContext(StepperContext);
  const [renderDash, setRenderDash] = useState<boolean>(false);
  useEffect(() => {
    setRenderDash(StepperSettings.stepperPhase);
  }, [StepperSettings.stepperPhase]);

  return (
    <Routes>
      <Route index element={renderDash ? <Dashboard /> : <Home />} />
      <Route path="/*" element={<NotFound />} />
    </Routes>
  );
}
