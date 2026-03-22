package com.project.demo.services.ProjectRoleAssignerService;

import com.project.demo.models.ProjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectRoleAssigner {

    public List<ProjectDTO> assignMissingRoles(List<ProjectDTO> projects) {
        // 1) Count how many times each professor is an encadrant
        Map<String, Long> encCount = projects.stream()
                .collect(Collectors.groupingBy(
                        ProjectDTO::getEncadrantEmail,
                        Collectors.counting()
                ));

        // 2) Build a map of required total presence = encCount * 3
        Map<String, Integer> requiredPresence = new HashMap<>();
        encCount.forEach((prof, cnt) -> requiredPresence.put(prof, (int)(cnt * 3)));

        // 3) Count current presence in all roles
        Map<String, Integer> currentPresence = new HashMap<>();
        for (ProjectDTO p : projects) {
            // encadrant
            currentPresence.merge(p.getEncadrantEmail(), 1, Integer::sum);
            // rapporteur (if set)
            if (StringUtils.hasText(p.getRapporteurEmail())) {
                currentPresence.merge(p.getRapporteurEmail(), 1, Integer::sum);
            }
            // president (if set)
            if (StringUtils.hasText(p.getPresidentEmail())) {
                currentPresence.merge(p.getPresidentEmail(), 1, Integer::sum);
            }
        }

        // 4) Create a queue of professors sorted by least currentPresence / requiredPresence ratio
        //    so those who are most “under‑assigned” get chosen first.
        class Prof {
            String email;
            int req;
            Prof(String e, int r) { email=e; req=r; }
        }
        Comparator<String> shortageComparator = Comparator.comparingInt(
                prof -> currentPresence.getOrDefault(prof, 0) * 100 / requiredPresence.get(prof)
        );

        // We'll re‑sort before each assignment
        List<String> professors = new ArrayList<>(requiredPresence.keySet());

        // 5) Fill in missing roles
        for (ProjectDTO p : projects) {
            // ensure maps have entries
            currentPresence.putIfAbsent(p.getEncadrantEmail(), 0);
            requiredPresence.putIfAbsent(p.getEncadrantEmail(), 0);

            // assign rapporteur if missing
            if (!StringUtils.hasText(p.getRapporteurEmail())) {
                String pick = pickProfessor(professors, requiredPresence, currentPresence, shortageComparator, p);
                p.setRapporteurEmail(pick);
                currentPresence.merge(pick, 1, Integer::sum);
            }

            // assign president if missing
            if (!StringUtils.hasText(p.getPresidentEmail())) {
                String pick = pickProfessor(professors, requiredPresence, currentPresence, shortageComparator, p);
                p.setPresidentEmail(pick);
                currentPresence.merge(pick, 1, Integer::sum);
            }
        }

        return projects;
    }

    /**
     * Pick a professor who:
     *  - has currentPresence < requiredPresence
     *  - is not already encadrant/rapporteur/president on this project
     */
    private String pickProfessor(List<String> professors,
                                 Map<String,Integer> requiredPresence,
                                 Map<String,Integer> currentPresence,
                                 Comparator<String> shortageComparator,
                                 ProjectDTO project)
    {
        // filter out those over‑assigned or already on this project
        List<String> candidates = professors.stream()
                .filter(prof -> currentPresence.getOrDefault(prof,0) < requiredPresence.getOrDefault(prof,0))
                .filter(prof -> !prof.equals(project.getEncadrantEmail()))
                .filter(prof -> !prof.equals(project.getRapporteurEmail()))
                .filter(prof -> !prof.equals(project.getPresidentEmail()))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No available professor to assign for project " + project.getCode());
        }

        // pick the one who is most under‑assigned
        candidates.sort(shortageComparator);
        return candidates.get(0);
    }
}
