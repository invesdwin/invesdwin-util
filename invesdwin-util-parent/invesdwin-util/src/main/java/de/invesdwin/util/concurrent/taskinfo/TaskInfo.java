package de.invesdwin.util.concurrent.taskinfo;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public class TaskInfo {

    private final String name;
    private final int createdCount;
    private final int startedCount;
    private final int completedCount;
    private final int tasksCount;
    private final Percent progress;

    public TaskInfo(final String name, final int createdCount, final int startedCount, final int completedCount,
            final int tasksCount, final Percent progress) {
        this.name = name;
        this.createdCount = createdCount;
        this.startedCount = startedCount;
        this.completedCount = completedCount;
        this.tasksCount = tasksCount;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public int getStartedCount() {
        return startedCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public boolean isCompleted() {
        return getCompletedCount() == getTasksCount();
    }

    public Percent getProgress() {
        return progress;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
