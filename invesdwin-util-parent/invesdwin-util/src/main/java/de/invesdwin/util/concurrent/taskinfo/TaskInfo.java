package de.invesdwin.util.concurrent.taskinfo;

import javax.annotation.concurrent.Immutable;

@Immutable
public class TaskInfo {

    private final String name;
    private final int createdCount;
    private final int runningCount;
    private final int completedCount;

    public TaskInfo(final String name, final int createdCount, final int runningCount, final int completedCount) {
        this.name = name;
        this.createdCount = createdCount;
        this.runningCount = runningCount;
        this.completedCount = completedCount;
    }

    public String getName() {
        return name;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getTasksCount() {
        return createdCount + runningCount + completedCount;
    }

    public boolean isCompleted() {
        return getCompletedCount() == getTasksCount();
    }

}
