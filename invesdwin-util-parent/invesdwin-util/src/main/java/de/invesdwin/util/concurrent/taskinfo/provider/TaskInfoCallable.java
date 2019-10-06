package de.invesdwin.util.concurrent.taskinfo.provider;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.priority.IPriorityCallable;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.taskinfo.TaskInfoManager;

@ThreadSafe
public final class TaskInfoCallable<V> implements IPriorityCallable<V>, ITaskInfoProvider {

    private final String name;
    private final Callable<V> delegate;
    private volatile TaskInfoStatus status;

    private TaskInfoCallable(final String name, final Callable<V> delegate) {
        this.name = name;
        this.delegate = delegate;
        this.status = TaskInfoStatus.CREATED;
        TaskInfoManager.register(this);
    }

    @Override
    public V call() throws Exception {
        this.status = TaskInfoStatus.RUNNING;
        try {
            return delegate.call();
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

    public static <T> TaskInfoCallable<T> of(final String name, final Callable<T> callable) {
        return new TaskInfoCallable<>(name, callable);
    }

}
