import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface InitialStateProps {
  isSideBarCollapsed: Boolean;
  isDarkMode: Boolean;
}

const initialState: InitialStateProps = {
  isSideBarCollapsed: false,
  isDarkMode : false
};

export const globalSlice = createSlice({
    name:"global",
    initialState,
    reducers:{
        setIsSideBarCollapsed:(state,action:PayloadAction<Boolean>)=>{
            state.isSideBarCollapsed = action.payload
        },
        setIsDarkmode: (state,action:PayloadAction<Boolean>)=>{
            state.isDarkMode = action.payload
        },
    },
}) 

export const { setIsSideBarCollapsed , setIsDarkmode } = globalSlice.actions;
export default globalSlice.reducer;