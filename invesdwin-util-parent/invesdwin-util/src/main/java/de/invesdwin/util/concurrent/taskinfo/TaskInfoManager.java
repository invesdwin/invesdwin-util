package de.invesdwin.util.concurrent.taskinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.taskinfo.provider.ITaskInfoProvider;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoStatus;
import de.invesdwin.util.concurrent.taskinfo.provider.WeakReferenceTaskInfoProvider;
import de.invesdwin.util.error.UnknownArgumentException;

/**
 * This class can be used to track long running tasks that should be visualized in a UI in some way. The taskInfos can
 * be polled periodically by the UI to display an indicator.
 */
@ThreadSafe
public final class TaskInfoManager {

    @GuardedBy("this.class")
    private static final Map<String, Map<Integer, WeakReferenceTaskInfoProvider>> NAME_TASKS = new HashMap<>();

    private TaskInfoManager() {}

    public static synchronized void register(final ITaskInfoProvider taskInfoProvider) {
        final String name = taskInfoProvider.getName();
        Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        if (tasks == null) {
            tasks = new HashMap<>();
        } else {
            final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
            if (taskInfo.isCompleted()) {
                tasks.clear();
            }
        }
        final WeakReferenceTaskInfoProvider weakReferenceTaskInfoProvider = new WeakReferenceTaskInfoProvider(
                taskInfoProvider);
        if (tasks.put(weakReferenceTaskInfoProvider.hashCode(), weakReferenceTaskInfoProvider) != null) {
            throw new IllegalStateException("Already registered: " + taskInfoProvider);
        }
    }

    public static synchronized void unregister(final ITaskInfoProvider taskInfoProvider) {
        final String name = taskInfoProvider.getName();
        final Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        if (tasks == null || tasks.remove(System.identityHashCode(taskInfoProvider)) == null) {
            throw new IllegalStateException("Not registered: " + taskInfoProvider);
        }
        final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
        if (taskInfo.isCompleted()) {
            Assertions.checkSame(NAME_TASKS.remove(name), tasks);
        }
    }

    public static synchronized List<TaskInfo> getTaskInfos() {
        final List<TaskInfo> taskInfos = new ArrayList<>();
        final Set<String> completedTaskInfoNames = new HashSet<>();
        for (final Entry<String, Map<Integer, WeakReferenceTaskInfoProvider>> entry : NAME_TASKS.entrySet()) {
            final String name = entry.getKey();
            final Collection<WeakReferenceTaskInfoProvider> tasks = entry.getValue().values();
            final TaskInfo taskInfo = getTaskInfo(name, tasks);
            if (taskInfo.isCompleted()) {
                completedTaskInfoNames.add(name);
            } else {
                taskInfos.add(taskInfo);
            }
        }
        for (final String completedTaskInfoName : completedTaskInfoNames) {
            Assertions.checkNotNull(NAME_TASKS.remove(completedTaskInfoName));
        }
        return taskInfos;
    }

    public static synchronized List<String> getTaskInfoNames() {
        return new ArrayList<>(NAME_TASKS.keySet());
    }

    public static synchronized TaskInfo getTaskInfo(final String name) {
        final Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        if (tasks == null) {
            return null;
        } else {
            final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
            if (taskInfo.isCompleted()) {
                Assertions.checkNotNull(NAME_TASKS.remove(name));
                return null;
            } else {
                return taskInfo;
            }
        }
    }

    private static TaskInfo getTaskInfo(final String name, final Collection<WeakReferenceTaskInfoProvider> tasks) {
        int createdCount = 0;
        int runningCount = 0;
        int completedCount = 0;
        for (final WeakReferenceTaskInfoProvider task : tasks) {
            final TaskInfoStatus status = task.getStatus();
            switch (status) {
            case CREATED:
                createdCount++;
                break;
            case RUNNING:
                runningCount++;
                break;
            case COMPLETED:
                completedCount++;
                break;
            default:
                throw UnknownArgumentException.newInstance(TaskInfoStatus.class, status);
            }
        }
        return new TaskInfo(name, createdCount, runningCount, completedCount);
    }
}
