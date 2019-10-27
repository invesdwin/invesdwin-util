package de.invesdwin.util.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.taskinfo.TaskInfoManager;
import de.invesdwin.util.concurrent.taskinfo.provider.ITaskInfoProvider;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoStatus;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;

@ThreadSafe
public class TaskInfoPendingCountListener implements IPendingCountListener, ITaskInfoProvider {

    private final String name;
    private volatile String description;
    private volatile boolean inheritable = true;
    private volatile TaskInfoStatus status = TaskInfoStatus.COMPLETED;

    private volatile int curPendingCount;
    private volatile int maxPendingCount;

    public TaskInfoPendingCountListener(final String name) {
        this.name = name;
    }

    @Override
    public synchronized void onPendingCountChanged(final int currentPendingCount) {
        curPendingCount = currentPendingCount;
        maxPendingCount = Integers.max(maxPendingCount, curPendingCount);
        if (status == TaskInfoStatus.COMPLETED) {
            if (curPendingCount > 0) {
                status = TaskInfoStatus.CREATED;
                TaskInfoManager.onCreated(this);
                status = TaskInfoStatus.STARTED;
                TaskInfoManager.onStarted(this);
            }
        } else if (currentPendingCount == 0) {
            status = TaskInfoStatus.COMPLETED;
            maxPendingCount = 0;
            TaskInfoManager.onCompleted(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public TaskInfoPendingCountListener withDescription(final String description) {
        this.description = description;
        return this;
    }

    public TaskInfoPendingCountListener withInheritable(final boolean inheritable) {
        Assertions.checkEquals(status, TaskInfoStatus.CREATED);
        this.inheritable = inheritable;
        return this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TaskInfoStatus getStatus() {
        return status;
    }

    @Override
    public Percent getProgress() {
        return new Percent(getCompletedCount(), maxPendingCount);
    }

    @Override
    public boolean isIneritable() {
        return inheritable;
    }

    @Override
    public int getCompletedCount() {
        return maxPendingCount - curPendingCount;
    }

    @Override
    public int getStartedCount() {
        return 0;
    }

    @Override
    public int getCreatedCount() {
        return curPendingCount;
    }

}
