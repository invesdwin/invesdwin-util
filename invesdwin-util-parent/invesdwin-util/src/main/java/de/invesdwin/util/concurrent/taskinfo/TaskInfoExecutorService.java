package de.invesdwin.util.concurrent.taskinfo;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListeningExecutorService;

import de.invesdwin.util.concurrent.ADelegateExecutorService;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoCallable;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoRunnable;

@ThreadSafe
public class TaskInfoExecutorService extends ADelegateExecutorService {

    private final String taskName;

    public TaskInfoExecutorService(final String taskName, final ListeningExecutorService delegate) {
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

    @Override
    protected <T> void maybeCancelled(final Callable<T> callable) {
        final TaskInfoCallable<T> taskInfo = (TaskInfoCallable<T>) callable;
        taskInfo.maybeCancelled();
    }

    @Override
    protected void maybeCancelled(final Runnable runnable) {
        final TaskInfoRunnable taskInfo = (TaskInfoRunnable) runnable;
        taskInfo.maybeCancelled();
    }

}
