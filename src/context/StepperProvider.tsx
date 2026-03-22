import React, { useState, createContext, useEffect } from "react";
import { boolean } from "zod";
interface stepperContextType {
  stepperPhase: boolean;
  setStepperPhase: any;
}
const StepperContext = createContext<stepperContextType>({
  stepperPhase: false,
  setStepperPhase: null,
});
function StepperProvider({ children: children }: { children: any }) {
  //stepperPhaseShould be pulled from user TODO
  const [stepperPhase, setStepperPhase] = useState<boolean>(false);

  return (
    <StepperContext.Provider
      value={{ stepperPhase: stepperPhase, setStepperPhase: setStepperPhase }}
    >
      {children}
    </StepperContext.Provider>
  );
}
export { StepperProvider, StepperContext };
