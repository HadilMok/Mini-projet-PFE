package com.project.demo.services.ProjectRoleAssignerService;

import com.project.demo.models.ProjectDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class FakeProjectGenerator {

    private static final String[] PROFESSORS = {
        "alice@uni.edu", "bob@uni.edu", "carol@uni.edu",
        "dave@uni.edu",  "eve@uni.edu", "frank@uni.edu"
    };
    private static final String[] STUDENTS = {
        "s1@student.edu","s2@student.edu","s3@student.edu","s4@student.edu",
        "s5@student.edu","s6@student.edu","s7@student.edu","s8@student.edu"
    };
    private final Random rnd = new Random();

    /**
     * Generate n ProjectDTOs with:
     * - name = "Project-"+UUID
     * - code = "C-"+i
     * - encadrantEmail random from PROFESSORS
     * - rapporteurEmail & presidentEmail null ~50% time
     * - studentEmails = two random distinct from STUDENTS
     */
    public List<ProjectDTO> generate(int n) {
        List<ProjectDTO> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            ProjectDTO p = new ProjectDTO();
            p.setName("Project-" + UUID.randomUUID().toString().substring(0, 8));
            p.setCode("C-" + i);

            // encadrant always set
            String enc = pick(PROFESSORS);
            p.setEncadrantEmail(enc);

            // rapporteur/president half the time left blank
            if (rnd.nextBoolean()) {
                p.setRapporteurEmail(pickExcept(PROFESSORS, enc));
            }
            if (rnd.nextBoolean()) {
                p.setPresidentEmail(pickExcept(PROFESSORS, enc));
            }

            // two distinct students
            String sA = pick(STUDENTS);
            String sB;
            do {
                sB = pick(STUDENTS);
            } while (sB.equals(sA));
            List<String> studs = List.of(sA, sB);
            p.setStudentEmails(studs);

            list.add(p);
        }
        return list;
    }

    private String pick(String[] arr) {
        return arr[rnd.nextInt(arr.length)];
    }

    private String pickExcept(String[] arr, String exclude) {
        String x;
        do {
            x = pick(arr);
        } while (x.equals(exclude));
        return x;
    }
}
