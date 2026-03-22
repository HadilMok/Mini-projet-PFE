package com.project.demo;

import com.project.demo.models.ProjectDTO;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.*;

public class FakeProjectGenerator {

    public static List<ProjectDTO> generate10Projects() {
        List<String> profs = List.of(
                "alice.prof@example.com",
                "bob.prof@example.com",
                "carol.prof@example.com",
                "dave.prof@example.com",
                "eve.prof@example.com",
                "eve1.prof@example.com",
                "eve2.prof@example.com"
        );

        return IntStream.rangeClosed(1, 10)
                .mapToObj(i -> {
                    ProjectDTO p = new ProjectDTO();
                    p.setName("Project " + i);
                    p.setCode("PJT-" + String.format("%03d", i));
                    p.setNote("Auto‑generated note for project " + i);
                    p.setEncadrantEmail(profs.get((i - 1) % profs.size()));
                    p.setRapporteurEmail(null);
                    p.setPresidentEmail(null);
                    p.setStudentEmails(List.of(
                            "student" + (2*i - 1) + "@example.edu",
                            "student" + (2*i)     + "@example.edu"
                    ));
                    return p;
                })
                .collect(Collectors.toList());
    }

    public static void fillMissingRoles(List<ProjectDTO> projects) {
        // 1) Build list of unique profs in stable order:
        List<String> profs = projects.stream()
                .map(ProjectDTO::getEncadrantEmail)
                .distinct()
                .collect(Collectors.toList());

        // 2) Calculate how many times each must appear total:
        //    totalAppearances = encCount * 3
        Map<String, Integer> encCount = new HashMap<>();
        for (ProjectDTO p : projects) {
            encCount.merge(p.getEncadrantEmail(), 1, Integer::sum);
        }
        Map<String, Integer> totalSlots = new HashMap<>();
        encCount.forEach((prof, cnt) -> totalSlots.put(prof, cnt * 3));

        // 3) Assign rapporteurs in round‑robin over profs (skip encadrant)
        int idx = 0;
        for (ProjectDTO p : projects) {
            String enc = p.getEncadrantEmail();
            if (!StringUtils.hasText(p.getRapporteurEmail())) {
                // find next prof not equal to enc
                for (int tried = 0; tried < profs.size(); tried++) {
                    String candidate = profs.get((idx++ ) % profs.size());
                    if (!candidate.equals(enc)) {
                        p.setRapporteurEmail(candidate);
                        totalSlots.merge(candidate, -1, Integer::sum);
                        break;
                    }
                }
            }
        }

        // 4) Assign presidents in a second round‑robin (skip encadrant)
        for (ProjectDTO p : projects) {
            String enc = p.getEncadrantEmail();
            if (!StringUtils.hasText(p.getPresidentEmail())) {
                for (int tried = 0; tried < profs.size(); tried++) {
                    String candidate = profs.get((idx++ ) % profs.size());
                    if (!candidate.equals(enc)) {
                        p.setPresidentEmail(candidate);
                        totalSlots.merge(candidate, -1, Integer::sum);
                        break;
                    }
                }
            }
        }

        // 5) (Optional) you can assert that all totalSlots values are now zero
        for (var e : totalSlots.entrySet()) {
            if (e.getValue() != 0) {
                throw new IllegalStateException("Mismatch for " + e.getKey() + ": slots left=" + e.getValue());
            }
        }
    }

    public static void main(String[] args) {
        List<ProjectDTO> projects = generate10Projects();
        System.out.println("Before filling:");
        projects.forEach(System.out::println);

        fillMissingRoles(projects);

        System.out.println("\nAfter filling:");
        projects.forEach(System.out::println);
    }
}
