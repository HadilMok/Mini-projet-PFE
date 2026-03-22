import React from "react";
import styles from "./Steps.module.css";
export default function Steps() {
  return (
    <div className={styles.container}>
      <ul>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            Add or remove professors to define your supervising staff.
          </p>
        </li>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            assigned to students. Add or remove PFE projects to be assigned to
            students.
          </p>
        </li>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            Add or remove students participating in the PFE.
          </p>
        </li>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            Manage professor availability at any time to ensure smooth
            coordination.
          </p>
        </li>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            assigned to students. Add or remove PFE projects to be assigned to
            students.
          </p>
        </li>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            Let our AI assist! Automatically fill empty project roles with the
            best matches.
          </p>
        </li>
        <li className={styles.li}>
          <div className={styles.dot}></div>
          <p className={styles.p}>
            Generate an optimized PFE schedule tailored for the entire period.
          </p>
        </li>
      </ul>
    </div>
  );
}
