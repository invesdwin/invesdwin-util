package de.invesdwin.util.concurrent.taskinfo.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.callable.ImmutableCallable;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.priority.IPriorityRunnable;
import de.invesdwin.util.concurrent.taskinfo.TaskInfoManager;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.scaled.Percent;

@ThreadSafe
public final class TaskInfoRunnable implements IPriorityRunnable, ITaskInfoProvider {

    private final String name;
    private volatile String description;
    private final Runnable delegate;
    private volatile TaskInfoStatus status;
    private final Callable<Percent> progress;
    private volatile boolean inheritable = true;

    private TaskInfoRunnable(final String name, final Runnable delegate, final Callable<Percent> progress) {
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

    public TaskInfoRunnable withDescription(final String description) {
        this.description = description;
        return this;
    }

    public TaskInfoRunnable withInheritable(final boolean inheritable) {
        Assertions.checkEquals(status, TaskInfoStatus.CREATED);
        this.inheritable = inheritable;
        return this;
    }

    @Override
    public void run() {
        this.status = TaskInfoStatus.STARTED;
        TaskInfoManager.onStarted(this);
        try {
            delegate.run();
        } finally {
            this.status = TaskInfoStatus.COMPLETED;
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
    public Percent getProgress() {
        try {
            return progress.call();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
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
        return of(name, runnable, null);
    }

    public static TaskInfoRunnable of(final String name, final Runnable runnable, final Callable<Percent> progress) {
        return new TaskInfoRunnable(name, runnable, progress);
    }

    public static List<TaskInfoRunnable> of(final String name, final Collection<? extends Runnable> tasks) {
        final List<TaskInfoRunnable> wrapped = new ArrayList<>(tasks.size());
        for (final Runnable task : tasks) {
            wrapped.add(TaskInfoRunnable.of(name, task));
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
