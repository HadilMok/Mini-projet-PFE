import React, { useEffect, useState } from "react";

import * as XLSX from "xlsx";
import styles from "./fileUPload.module.css";

interface filePropsType {
  setData: any;
  dataKey: string;
}
interface converterType {
  firstname?: string;
  lastname?: string;
  email?: string;
  id?: string;
}
interface projectType {
  firstName?: string;
  lastName?: string;
  email?: string;
  id?: number;
}
export default function FileUpload(fileProps: filePropsType) {
  const [data, setData] = useState<any[]>([]);
  const [file, setFile] = useState<object>({ name: "" });
  const dataKey = fileProps.dataKey;
  const dataSetter = fileProps.setData;
  const converter: converterType = {
    firstname: "firstName",
    lastname: "lastName",
    id: "id",
    email: "email",
  };
  function convertData() {
    let reformedData = [];
    if (data && data.length) {
      const headerArray = data[0];
      data.shift();
      for (let p of data) {
        console.log(p);
        let person: projectType = {};
        let index = 0;
        for (let attribute of p) {
          const keyAtt = headerArray[index++].toLowerCase().trim();
          if (converter.hasOwnProperty(keyAtt))
            person[converter[keyAtt]] = attribute;
        }
        reformedData.push(person);
      }

      dataSetter((prev: []) => {
        localStorage.setItem(
          dataKey,
          JSON.stringify([...prev, ...reformedData])
        );
        return [...prev, ...reformedData];
      });
    }
  }

  function fileLoader(e: any) {
    const bin = e.target?.result;
    const dataFile = XLSX.read(bin, { type: "binary" });
    //take first sheet in
    const sheet = dataFile.Sheets[dataFile.SheetNames[0]];
    //convert this sheeet to json
    const dataResult = XLSX.utils.sheet_to_json(sheet, { header: 1 });
    setData(dataResult);
  }

  useEffect(() => {
    console.log("data");
    console.log(data);
    convertData();
  }, [data]);
  function extractExtension(name?: string) {
    if (!name || !name.length) return "";
    let len = name.length - 1;
    let extension = "";
    while (len-- > -1) {
      if (name[len] == ".") break;
      extension += name[len];
    }
    return extension.split("").reverse().join("");
  }
  function handleFile(e: any) {
    const file = e.target.files?.[0];
    console.log(file);
    if (file) {
      //safety check
      const extension = extractExtension(file.name?.toLowerCase());

      if (extension != "xlsx" && extension != "xls") return;
      // prep and read
      setFile(file);
      const reader = new FileReader();
      reader.onload = fileLoader;
      reader.readAsArrayBuffer(file);
    }
  }

  return (
    <div className={styles.container}>
      <input
        className={styles.input}
        type="file"
        accept=".xlsx, .xls"
        onChange={handleFile}
        placeholder="hi"
      />
      <p className={styles.par}>{file.name}</p>
    </div>
  );
}
