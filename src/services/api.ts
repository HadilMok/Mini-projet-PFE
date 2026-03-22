import axios from "axios";

async function Post(url: string, data: object) {
  let ret = { status: 500, ok: 0, message: "Internal error: Timeout" };
  await axios
    .post(url, data)
    .then((res) => {
      ret = res.data;
    })
    .catch((e) => {
      ret = {
        status: 500,
        ok: 0,
        message: "Network error: " + e.message + " STATUS -> " + e.status,
      };
    });

  return ret;
}

async function Get(url: string, data: object) {
  let ret = [];
  await axios
    .get(url, data)
    .then((res) => {
      ret = res;
    })
    .catch((e) => {
      ret = [];
    });

  return ret;
}

async function Delete(url: string, data: object) {
  let ret = { status: 500, ok: 0, message: "Internal error: Timeout" };
  await axios
    .delete(url, data)
    .then((res) => {
      ret = res.data;
    })
    .catch((e) => {
      ret = {
        status: 500,
        ok: 0,
        message: "Network error: " + e.message + " STATUS -> " + e.status,
      };
    });

  return ret;
}

async function Patch(url: string, data: object) {
  let ret = { status: 500, ok: 0, message: "Internal error: Timeout" };
  await axios
    .post(url, data)
    .then((res) => {
      ret = res.data;
    })
    .catch((e) => {
      ret = {
        status: 500,
        ok: 0,
        message: "Network error: " + e.message + " STATUS -> " + e.status,
      };
    });

  return ret;
}

export { Get, Post, Patch, Delete };
