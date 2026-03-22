package com.project.demo.controllers.StudentController;


import com.project.demo.models.Enumerations.Role;
import com.project.demo.models.Student;
import com.project.demo.services.StudentService.StudentService;
import com.project.demo.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService service;

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        student.setPassword(Utils.generatePassword());
        student.setPhone("");
        student.setRole(Role.STUDENT);
        return ResponseEntity.ok(service.createOrUpdateStudent(student));
    }

    // This endpoint t9bl list w trj3 progress
    @PostMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter uploadWithProgress(@RequestBody List<Student> students) {
        SseEmitter emitter = new SseEmitter(/* optional timeout ms */);

        // offload processing so we can return the emitter immediately
        CompletableFuture.runAsync(() -> {
            try {
                int total = students.size();
                for (int i = 0; i < total; i++) {
                    service.saveSingle(students.get(i));   // save one student
                    double pct = (i + 1) * 100.0 / total;

                    // send a “progress” event with the percentage
                    emitter.send(SseEmitter.event()
                            .name("progress")
                            .data(pct));
                }

                // final “complete” event
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("All done"));
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }



    /*
    *
    * partie @Anas
    *
    * */
    /*import React, { useState, useEffect } from 'react';
import axios from 'axios';

interface StudentDTO {
  firstName: string;
  lastName: string;
  email: string;
  inscriptionNumber: number;
  projectId: number;
}

export function BulkUploadPage() {
  const [students, setStudents] = useState<StudentDTO[]>();
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState<'idle'|'uploading'|'complete'>('idle');

  const startUpload = async () => {
        setStatus('uploading');

        // 1) kick off SSE listener
    const es = new EventSource('/api/students/bulk/progress');
        es.addEventListener('progress', (e: MessageEvent) => {
            setProgress(Number(e.data));
        });
        es.addEventListener('complete', () => {
                setStatus('complete');
        es.close();
    });
        es.onerror = () => {

                es.close();
    };


        await axios.post('/api/students/bulk/progress', students, {
                headers: { 'Content-Type': 'application/json' }
    });
    };

  return (
    <div style={{ width: 400, margin: '2rem auto' }}>
      <button onClick={startUpload} disabled={status==='uploading'}>
    {status === 'complete' ? 'Done!' : 'Start Upload'}
      </button>

    {status !== 'idle' && (
            <div style={{ marginTop: '1rem' }}>
          <div>Progress: {progress.toFixed(1)}%</div>
            <div style={{
            width: '100%',
            height: 12,
            background: '#eee',
            borderRadius: 6,
            overflow: 'hidden',
            marginTop: 4
          }}>
            <div style={{
            width: `${progress}%`,
        height: '100%',
                background: '#4caf50'
            }}/>
          </div>
            </div>
      )}
    </div>
            );
}
*/

    @GetMapping
    ResponseEntity<List<Student>> getAllStudent() {
        return ResponseEntity.ok(service.getAllStudent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(service.getStudent(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable("id") Long id
    ) {
        service.deleteStudent(id);
        return ResponseEntity.accepted().build();
    }

    @PutMapping
    ResponseEntity<Student> updateEtudiant(@RequestBody Student student) {
        return ResponseEntity.ok(service.createOrUpdateStudent(student));
    }

    @GetMapping("/getPassword")
    public ResponseEntity<String> getPassword(@RequestBody String email) {
        return ResponseEntity.ok(service.getPassword(email));
    }

    @PostMapping("/populate")
    public ResponseEntity<Void> populateStudent(@RequestBody StudentRequest studentRequest) {
        service.populateStudent(studentRequest);
        return ResponseEntity.accepted().build();
    }

}
