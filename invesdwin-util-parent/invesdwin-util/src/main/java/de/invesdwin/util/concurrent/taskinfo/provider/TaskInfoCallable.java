package de.invesdwin.util.concurrent.taskinfo.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lambda.callable.ImmutableCallable;
import de.invesdwin.util.concurrent.priority.IPriorityCallable;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.taskinfo.TaskInfoManager;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.scaled.Percent;

@ThreadSafe
public final class TaskInfoCallable<V> implements IPriorityCallable<V>, ITaskInfoProvider {

    private final String name;
    private volatile String description;
    private final Callable<V> delegate;
    private volatile TaskInfoStatus status;
    private final Callable<Percent> progress;
    private volatile boolean inheritable = true;

    private TaskInfoCallable(final String name, final Callable<V> delegate, final Callable<Percent> progress) {
        this.name = name;
        this.delegate = delegate;
        if (progress == null) {
            this.progress = new ImmutableCallable<Percent>(null);
        } else {
            this.progress = progress;
        }
        this.status = TaskInfoStatus.CREATED;
        TaskInfoManager.onCreated(this);
    }

    public TaskInfoCallable<V> withDescription(final String description) {
        this.description = description;
        return this;
    }

    public TaskInfoCallable<V> withInheritable(final boolean inheritable) {
        Assertions.checkEquals(status, TaskInfoStatus.CREATED);
        this.inheritable = inheritable;
        return this;
    }

    @Override
    public V call() throws Exception {
        this.status = TaskInfoStatus.STARTED;
        TaskInfoManager.onStarted(this);
        try {
            return delegate.call();
        } finally {
            this.status = TaskInfoStatus.COMPLETED;
            TaskInfoManager.onCompleted(this);
        }
    }

    public void maybeCancelled() {
        if (status == TaskInfoStatus.STARTED) {
            TaskInfoManager.onCompleted(this);
        }
    }

    @Override
    public String getName() {
        return name;
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
        try {
            return progress.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getPriority() {
        if (delegate instanceof IPriorityProvider) {
            final IPriorityProvider cDelegate = (IPriorityProvider) delegate;
            return cDelegate.getPriority();
        }
        return MISSING_PRIORITY;
    }

    public static <T> Callable<T> ofNullable(final String name, final Callable<T> callable) {
        return ofNullable(name, callable, null);
    }

    public static <T> Callable<T> ofNullable(final String name, final Callable<T> callable,
            final Callable<Percent> progress) {
        if (name == null) {
            return callable;
        } else {
            return of(name, callable, progress);
        }
    }

    public static <T> List<? extends Callable<T>> ofNullable(final String name,
            final Collection<? extends Callable<T>> tasks) {
        if (name == null) {
            return new ArrayList<>(tasks);
        } else {
            return of(name, tasks);
        }
    }

    public static <T> TaskInfoCallable<T> of(final String name, final Callable<T> callable) {
        return of(name, callable, null);
    }

    public static <T> TaskInfoCallable<T> of(final String name, final Callable<T> callable,
            final Callable<Percent> progress) {
        return new TaskInfoCallable<>(name, callable, progress);
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

    @Override
    public boolean isIneritable() {
        return inheritable;
    }

}
