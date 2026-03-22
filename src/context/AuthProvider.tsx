import React, { createContext, useEffect, useState } from "react";
import {
  AuthContextSettingsInterface,
  AuthContextInterface,
} from "../types/AuthProviderTypes";
import axios from "axios";
const AuthContext = createContext<AuthContextSettingsInterface>({
  user: null,
  setUser: null,
  userToken: null,
  setUserToken: null,
});

// headers: { Authorization: userToken }
const AuthProvider = (props: AuthContextInterface) => {
  const [userToken, setUserToken] = useState<string>(
    localStorage.getItem("userToken")
  );
  const [user, setUser] = useState<object | null>(
    localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user"))
      : null
  );

  function saveToken(token: any, user: any) {
    if (token) {
      localStorage.setItem("userToken", token);
      localStorage.setItem("user", JSON.stringify(user));
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    } else {
      localStorage.removeItem("userToken");
      localStorage.removeItem("user");
      delete axios.defaults.headers.common["Authorization"];
    }
  }

  useEffect(() => {
    console.log(userToken);
    if (!userToken) {
      //logged out or expired

      //delete token and user
      console.log("signed nullified");
      saveToken(null, null);
    } else {
      //logged in
      console.log("signed");
      saveToken(userToken, user);
    }
  }, [userToken]);

  return (
    <AuthContext.Provider
      value={{
        user: user,
        setUser: setUser,
        userToken: userToken,
        setUserToken: setUserToken,
      }}
    >
      {props.children}
    </AuthContext.Provider>
  );
};

export { AuthContext, AuthProvider };
