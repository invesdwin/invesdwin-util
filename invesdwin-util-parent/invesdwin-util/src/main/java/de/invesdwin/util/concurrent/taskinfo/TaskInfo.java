package de.invesdwin.util.concurrent.taskinfo;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public class TaskInfo {

    private final String name;
    private final int createdCount;
    private final int startedCount;
    private final int completedCount;

    public TaskInfo(final String name, final int createdCount, final int startedCount, final int completedCount) {
        this.name = name;
        this.createdCount = createdCount;
        this.startedCount = startedCount;
        this.completedCount = completedCount;
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
        return createdCount + startedCount + completedCount;
    }

    public boolean isCompleted() {
        return getCompletedCount() == getTasksCount();
    }

    public Percent getProgress() {
        final int tasksCount = getTasksCount();
        if (tasksCount > 1) {
            return new Percent(getCompletedCount(), tasksCount);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
