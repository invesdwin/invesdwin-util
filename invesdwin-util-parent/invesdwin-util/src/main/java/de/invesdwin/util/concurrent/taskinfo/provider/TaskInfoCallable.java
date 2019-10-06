package de.invesdwin.util.concurrent.taskinfo.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.priority.IPriorityCallable;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.taskinfo.TaskInfoManager;
import de.invesdwin.util.lang.Objects;

@ThreadSafe
public final class TaskInfoCallable<V> implements IPriorityCallable<V>, ITaskInfoProvider {

    private final String name;
    private final Callable<V> delegate;
    private volatile TaskInfoStatus status;

    private TaskInfoCallable(final String name, final Callable<V> delegate) {
        this.name = name;
        this.delegate = delegate;
        this.status = TaskInfoStatus.CREATED;
        TaskInfoManager.onCreated(this);
    }

    @Override
    public V call() throws Exception {
        this.status = TaskInfoStatus.STARTED;
        TaskInfoManager.onStarted(this);
        try {
            return delegate.call();
        } finally {
            TaskInfoManager.onCompleted(this);
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

    public static <T> List<TaskInfoCallable<T>> of(final String taskName,
            final Collection<? extends Callable<T>> tasks) {
        final List<TaskInfoCallable<T>> wrapped = new ArrayList<>(tasks.size());
        for (final Callable<T> task : tasks) {
            wrapped.add(TaskInfoCallable.of(taskName, task));
        }
        return wrapped;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name).add("identity", hashCode()).toString();
    }

}
