package com.orchex.app.workflow.handler;

import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.handler.exception.TaskHandlerNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TaskHandlerRegistry {

    private final Map<TaskType, TaskHandler> handlerMap;

    public TaskHandlerRegistry(List<TaskHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(TaskHandler::getTaskType, handler -> handler));
    }

    public TaskHandler getHandler(TaskType taskType) {
        TaskHandler handler = handlerMap.get(taskType);
        if (handler == null) throw new TaskHandlerNotFoundException(taskType);
        return handler;
    }
}
