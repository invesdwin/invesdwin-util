package de.invesdwin.util.concurrent.taskinfo.provider;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.priority.IPriorityRunnable;
import de.invesdwin.util.concurrent.taskinfo.TaskInfoManager;

@ThreadSafe
public final class TaskInfoRunnable implements IPriorityRunnable, ITaskInfoProvider {

    private final String name;
    private final Runnable delegate;
    private volatile TaskInfoStatus status;

    private TaskInfoRunnable(final String name, final Runnable delegate) {
        this.name = name;
        this.delegate = delegate;
        this.status = TaskInfoStatus.CREATED;
        TaskInfoManager.register(this);
    }

    @Override
    public void run() {
        this.status = TaskInfoStatus.RUNNING;
        try {
            delegate.run();
        } finally {
            TaskInfoManager.unregister(this);
            this.status = TaskInfoStatus.COMPLETED;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TaskInfoStatus getStatus() {
        return status;
    }

    @Override
    public double getPriority() {
        if (delegate instanceof IPriorityProvider) {
            final IPriorityProvider cDelegate = (IPriorityProvider) delegate;
            return cDelegate.getPriority();
        }
        return MISSING_PRIORITY;
    }

    public static TaskInfoRunnable of(final String name, final Runnable runnable) {
        return new TaskInfoRunnable(name, runnable);
    }

}
