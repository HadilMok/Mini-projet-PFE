import React, { useState, createContext } from "react";
import { boolean } from "zod";
interface PopupContextType {
  popup: boolean;
  setPopup: any;
}
const PopupContext = createContext<PopupContextType>({
  popup: false,
  setPopup: null,
});
function PopupProvider({ children: children }: { children: any }) {
  const [popup, setPopup] = useState<boolean>(false);
  return (
    <PopupContext.Provider value={{ popup: popup, setPopup: setPopup }}>
      {children}
    </PopupContext.Provider>
  );
}
export { PopupProvider, PopupContext };
