package de.invesdwin.util.concurrent.taskinfo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.ADelegateExecutorService;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoCallable;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoRunnable;

@ThreadSafe
public class TaskInfoExecutorService extends ADelegateExecutorService {

    private final String taskName;

    public TaskInfoExecutorService(final String taskName, final ExecutorService delegate) {
        super(delegate);
        this.taskName = taskName;
    }

    @Override
    protected Runnable newRunnable(final Runnable runnable) {
        return TaskInfoRunnable.of(taskName, runnable);
    }

    @Override
    protected <T> Callable<T> newCallable(final Callable<T> callable) {
        return TaskInfoCallable.of(taskName, callable);
    }

}
