package com.project.demo.services.ProjectRoleAssignerService;

import com.project.demo.models.ProjectDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ProjectRoleFillerService {

    /**
     * For each project in the list, if rapporteurEmail or presidentEmail is blank,
     * assign one from the pool of professors.  Each professor's total presences
     * (encadrant + rapporteur + president) will equal encadrantCount * 3.
     */

}
