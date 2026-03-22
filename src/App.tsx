import { useContext, useEffect, useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import "./App.css";
import Login from "./pages/login/login";
import { BrowserRouter as Router, Route } from "react-router-dom";
import { Routes } from "react-router";
import SignUp from "./pages/signup/signup";
import Home from "./pages/home/home";
import NotFound from "./pages/fallback/notfound";
import Layout from "./components/layout/Layout";
import StoreProvider from "./context/redux";
import { AuthContext } from "./context/AuthProvider";
import Forgot from "./pages/forgot/forgot";
import HomeApp from "./pages/home/App";
import { PopupContext } from "./context/PopupProvider";
import Shade from "./components/common/shader";
import { Settings } from "lucide-react";

function App() {
  //change auth and work on home page
  //addrouting
  const AuthSettings = useContext(AuthContext);
  const PopupSettings = useContext(PopupContext);
  const [shader, setShader] = useState<boolean>(false);

  const [auth, setAuth] = useState<Boolean>(
    AuthSettings.userToken ? true : false
  );

  // const navigate = useNavigate();

  useEffect(() => {
    //   navigate("/login");
    console.log(AuthSettings.userToken + "done ");
    if (AuthSettings.userToken) setAuth(true);
    else setAuth(false);
  }, [AuthSettings.userToken]);
  useEffect(() => {
    console.log(AuthSettings.userToken + "done ");
  }, []);

  useEffect(() => {
    setShader(PopupSettings.popup);
  }, [PopupSettings.popup]);
  return (
    <div>
      {shader && <Shade />}
      <Router>
        <Routes>
          {!auth ? (
            <>
              <Route index element={<Login />} />
              <Route path="/signup/*" element={<SignUp />} />
              <Route path="/forgot/*" element={<Forgot />} />
            </>
          ) : (
            <>
              <Route index element={<HomeApp />} />

              <Route path="settings" element={<Settings />} />
            </>
          )}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
    </div>
  );
}
{
  /* <>
      <StoreProvider>
        <Router>
          <Routes>
            <Route path="/signup/*" element={<SignUp />} />
            <Route path="/signin" element={<Login />} />
            {auth ? (
              <Route element={<Layout />}>
                <Route path="/" element={<Home />} />
              </Route>
            ) : (
              <>
                <Route path="/signin" element={<Login />} />
              </>
            )}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Router>
      </StoreProvider>
    </> */
}

export default App;
