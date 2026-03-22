import { DataTable } from "@/components/common/data-table/data-table";
import { Button } from "@/components/ui/button";
import { AuthContext } from "@/context/AuthProvider";
import React, { useContext, useEffect, useState } from "react";
import { Link, Route, Routes, useNavigate } from "react-router-dom";
//import data from "@/app/dashboard/data.json";
import FileUpload from "@/components/specific/fileUpload/fileUpload";
import Steppers from "@/components/specific/steppers/steppers";
import Dashboard from "../dashboard/dashboard";
function Home() {
  const [steppers, setSteppers] = useState<Boolean>(true);
  const navigate = useNavigate();
  const AuthSettings = useContext(AuthContext);

  function logout() {
    // AuthSettings.setUser(null)
    AuthSettings.setUserToken(null);
    navigate("/");
  }

  return (
    <>
      <Button onClick={logout}>Logout</Button>
      {steppers ? <Steppers /> : <Dashboard />}
    </>
  );
}

export default Home;
