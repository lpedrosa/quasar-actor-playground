package io.github.lpedrosa.worker.messages;

public final class DoSomeWork implements WorkerCallMessage {
    private final String taskType;

    public DoSomeWork(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return taskType;
    }
}
