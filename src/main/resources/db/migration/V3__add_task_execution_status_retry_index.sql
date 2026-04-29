CREATE INDEX idx_task_execution_status_next_retry
    ON task_executions (status, next_retry_at);
