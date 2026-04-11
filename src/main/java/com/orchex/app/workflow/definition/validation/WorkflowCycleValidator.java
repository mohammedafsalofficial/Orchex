package com.orchex.app.workflow.definition.validation;

import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.dto.TaskDefinitionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.*;

public class WorkflowCycleValidator implements ConstraintValidator<ValidWorkflow, CreateWorkflowRequest> {

    @Override
    public void initialize(ValidWorkflow constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateWorkflowRequest dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getTasks() == null || dto.getTasks().isEmpty()) return true;

        context.disableDefaultConstraintViolation();

        Set<String> taskNames = new HashSet<>();
        List<String> errors = new ArrayList<>();

        collectNamesAndCheckDuplicates(dto, taskNames, errors);

        Map<String, List<String>> adjList = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        buildGraph(dto, taskNames, adjList, inDegree, errors);

        if (!errors.isEmpty()) return fail(context, errors);

        return detectCycle(taskNames, adjList, inDegree, context);
    }

    /**
     * Collect task names and detect duplicates
     */
    private void collectNamesAndCheckDuplicates(
            CreateWorkflowRequest dto,
            Set<String> taskNames,
            List<String> errors
    ) {
        for (TaskDefinitionRequest task : dto.getTasks()) {
            if (task.getName() == null) continue;
            if (!taskNames.add(task.getName())) {
                errors.add("Duplicate task name: '" + task.getName() + "'");
            }
        }
    }

    /**
     * Validate dependency references, detect self-deps, build graph
     */
    private void buildGraph(
            CreateWorkflowRequest dto,
            Set<String> taskNames,
            Map<String, List<String>> adjList,
            Map<String, Integer> inDegree,
            List<String> errors
    ) {
        for (String name : taskNames) {
            adjList.put(name, new ArrayList<>());
            inDegree.put(name, 0);
        }

        for (TaskDefinitionRequest task : dto.getTasks()) {
            if (task.getName() == null || task.getDependencies() == null) continue;

            for (String dep : task.getDependencies()) {
                if (dep == null) continue;

                if (dep.equals(task.getName())) {
                    errors.add("Task '" + task.getName() + "' depends on itself");
                    continue;
                }

                if (!taskNames.contains(dep)) {
                    errors.add("Task '" + task.getName() + "' depends on unknown task '" + dep + "'");
                    continue;
                }

                // Forward edge: dep → task (dep must complete before task runs)
                adjList.get(dep).add(task.getName());
                inDegree.merge(task.getName(), 1, Integer::sum);
            }
        }
    }

    /**
     * Cycle detection via Kahn's algorithm (BFS topological sort)
     */
    private boolean detectCycle(
            Set<String> taskNames,
            Map<String, List<String>> adjList,
            Map<String, Integer> inDegree,
            ConstraintValidatorContext context
    ) {
        Queue<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) queue.add(entry.getKey());
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            String current = queue.poll();
            processed++;

            for (String dependent : adjList.get(current)) {
                int remaining = inDegree.merge(dependent, -1, Integer::sum);
                if (remaining == 0) queue.add(dependent);
            }
        }

        if (processed != taskNames.size()) {
            Set<String> cycleNodes = new LinkedHashSet<>();
            for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
                if (entry.getValue() > 0) cycleNodes.add(entry.getKey());
            }
            return fail(context, List.of("Circular dependency detected among tasks: " + cycleNodes));
        }

        return true;
    }

    // --- Failure helpers ---

    private boolean fail(ConstraintValidatorContext context, List<String> errors) {
        errors.forEach(msg ->
                context.buildConstraintViolationWithTemplate(msg).addConstraintViolation()
        );
        return false;
    }

    private boolean fail(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
