package de.invesdwin.util.concurrent.taskinfo;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.math.decimal.scaled.Percent;

@Immutable
public class TaskInfo extends AValueObject {

    private final String name;
    private final int createdCount;
    private final int startedCount;
    private final int completedCount;
    private final int tasksCount;
    private final Percent progress;
    private final Set<String> descriptions;

    public TaskInfo(final String name, final int createdCount, final int startedCount, final int completedCount,
            final int tasksCount, final Percent progress, final Set<String> descriptions) {
        this.name = name;
        this.createdCount = createdCount;
        this.startedCount = startedCount;
        this.completedCount = completedCount;
        this.tasksCount = tasksCount;
        this.progress = progress;
        this.descriptions = descriptions;
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

    public Set<String> getDescriptions() {
        return descriptions;
    }

}
