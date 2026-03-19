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
        if (dto.getTasks() == null) return true;

        Map<String, List<String>> graph = new HashMap<>();
        Set<String> taskNames = new HashSet<>();

        // Check for duplicate task names
        for (TaskDefinitionRequest task : dto.getTasks()) {
            String taskName = task.getName();
            if (!taskNames.add(taskName)) {
                return fail(context, "Duplicate task name found: " + taskName);
            }
            graph.put(taskName, new ArrayList<>());
        }

        // Validate dependencies reference existing tasks, then build graph
        for (TaskDefinitionRequest task : dto.getTasks()) {
            String taskName = task.getName();
            List<String> dependencies = task.getDependencies();

            if (dependencies != null) {
                for (String dependency : dependencies) {
                    if (!taskNames.contains(dependency)) {
                        return fail(context,
                                String.format("Task '%s' depends on unknown task '%s'", taskName, dependency));
                    }
                }
                graph.get(taskName).addAll(dependencies);
            }
        }

        // Detect cycle using DFS
        Set<String> visiting = new HashSet<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            if (hasCycle(node, graph, visiting, visited)) {
                return fail(context, "Workflow contains cyclic dependency involving task: " + node);
            }
        }

        return true;
    }

    private boolean hasCycle(
            String node,
            Map<String, List<String>> graph,
            Set<String> visiting,
            Set<String> visited
    ) {
        if (visited.contains(node)) return false;
        if (visiting.contains(node)) return true;

        visiting.add(node);

        for (String neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (hasCycle(neighbor, graph, visiting, visited)) {
                return true;
            }
        }

        visiting.remove(node);
        visited.add(node);

        return false;
    }

    private boolean fail(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
