package de.invesdwin.util.concurrent.taskinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.fast.IFastIterableSet;
import de.invesdwin.util.concurrent.taskinfo.provider.ITaskInfoProvider;
import de.invesdwin.util.concurrent.taskinfo.provider.TaskInfoStatus;
import de.invesdwin.util.concurrent.taskinfo.provider.WeakReferenceTaskInfoProvider;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.scaled.Percent;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * This class can be used to track long running tasks that should be visualized in a UI in some way. The taskInfos can
 * be polled periodically by the UI to display an indicator.
 */
@ThreadSafe
public final class TaskInfoManager {

    @GuardedBy("this.class")
    private static final Map<String, Map<Integer, WeakReferenceTaskInfoProvider>> NAME_TASKS = new LinkedHashMap<>();
    private static final IFastIterableSet<ITaskInfoListener> LISTENERS = ILockCollectionFactory.getInstance(true)
            .newFastIterableLinkedSet();
    private static final FastThreadLocal<Stack<WeakReferenceTaskInfoProvider>> CURRENT_THREAD_TASK_INFO_NAME = new FastThreadLocal<>();

    private TaskInfoManager() {}

    public static synchronized void onCreated(final ITaskInfoProvider taskInfoProvider) {
        final String name = taskInfoProvider.getName();
        Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        boolean added = false;
        if (tasks == null) {
            tasks = new HashMap<>();
            NAME_TASKS.put(name, tasks);
            added = true;
        } else {
            final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
            if (taskInfo.isCompleted()) {
                tasks.clear();
                triggerOnTaskInfoRemoved(name);
                added = true;
            }
        }
        final int identityHashCode = System.identityHashCode(taskInfoProvider);
        final WeakReferenceTaskInfoProvider weakReferenceTaskInfoProvider = new WeakReferenceTaskInfoProvider(
                identityHashCode, taskInfoProvider);
        if (tasks.put(identityHashCode, weakReferenceTaskInfoProvider) != null) {
            throw new IllegalStateException("Already registered: " + taskInfoProvider);
        }
        if (added) {
            triggerOnTaskInfoAdded(name);
        }
    }

    public static void onStarted(final ITaskInfoProvider taskInfoProvider) {
        Stack<WeakReferenceTaskInfoProvider> taskInfoNameList = CURRENT_THREAD_TASK_INFO_NAME.get();
        if (taskInfoNameList == null) {
            taskInfoNameList = new Stack<WeakReferenceTaskInfoProvider>();
            CURRENT_THREAD_TASK_INFO_NAME.set(taskInfoNameList);
        }
        final int identityHashCode = System.identityHashCode(taskInfoProvider);
        taskInfoNameList.push(new WeakReferenceTaskInfoProvider(identityHashCode, taskInfoProvider));
    }

    private static void triggerOnTaskInfoAdded(final String name) {
        final ITaskInfoListener[] array = LISTENERS.asArray(ITaskInfoListener.class);
        for (int i = 0; i < array.length; i++) {
            array[i].onTaskInfoAdded(name);
        }
    }

    private static void triggerOnTaskInfoRemoved(final String name) {
        final ITaskInfoListener[] array = LISTENERS.asArray(ITaskInfoListener.class);
        for (int i = 0; i < array.length; i++) {
            array[i].onTaskInfoRemoved(name);
        }
    }

    public static synchronized void onCompleted(final ITaskInfoProvider taskInfoProvider) {
        final String name = taskInfoProvider.getName();
        final Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        if (tasks == null) {
            throw new IllegalStateException("Not registered: " + taskInfoProvider);
        }
        final Stack<WeakReferenceTaskInfoProvider> taskInfoNameList = CURRENT_THREAD_TASK_INFO_NAME.get();
        while (taskInfoNameList != null && !taskInfoNameList.isEmpty()) {
            final WeakReferenceTaskInfoProvider peek = taskInfoNameList.peek();
            if (peek.equals(taskInfoProvider) || peek.getStatus() == TaskInfoStatus.COMPLETED) {
                taskInfoNameList.pop();
            } else {
                break;
            }
        }
        final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
        if (taskInfo.isCompleted()) {
            CURRENT_THREAD_TASK_INFO_NAME.remove();
            Assertions.checkSame(NAME_TASKS.remove(name), tasks);
            triggerOnTaskInfoRemoved(name);
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
        for (final String name : completedTaskInfoNames) {
            Assertions.checkNotNull(NAME_TASKS.remove(name));
            triggerOnTaskInfoRemoved(name);
        }
        return taskInfos;
    }

    public static synchronized List<String> getTaskInfoNames() {
        return new ArrayList<>(NAME_TASKS.keySet());
    }

    public static String getCurrentThreadTaskInfoName() {
        final Stack<WeakReferenceTaskInfoProvider> taskInfoNameList = CURRENT_THREAD_TASK_INFO_NAME.get();
        if (taskInfoNameList == null) {
            return null;
        }
        while (!taskInfoNameList.isEmpty()) {
            if (taskInfoNameList.peek().getStatus() == TaskInfoStatus.COMPLETED) {
                taskInfoNameList.pop();
            } else {
                break;
            }
        }
        if (taskInfoNameList.isEmpty()) {
            CURRENT_THREAD_TASK_INFO_NAME.remove();
            return null;
        } else {
            return taskInfoNameList.peek().getName();
        }
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
        int startedCount = 0;
        int completedCount = 0;
        int tasksCount = 0;
        double sumProgressRate = 0;
        for (final WeakReferenceTaskInfoProvider task : tasks) {
            tasksCount++;
            final TaskInfoStatus status = task.getStatus();
            switch (status) {
            case CREATED:
                createdCount++;
                break;
            case STARTED:
                startedCount++;
                break;
            case COMPLETED:
                completedCount++;
                break;
            default:
                throw UnknownArgumentException.newInstance(TaskInfoStatus.class, status);
            }
            if (status == TaskInfoStatus.COMPLETED) {
                sumProgressRate += 1D;
            } else {
                final Percent progress = task.getProgress();
                if (progress != null) {
                    sumProgressRate += progress.getRate();
                }
            }
        }
        return new TaskInfo(name, createdCount, startedCount, completedCount, tasksCount,
                new Percent(sumProgressRate, tasksCount));
    }

    public static IFastIterableSet<ITaskInfoListener> getListeners() {
        return LISTENERS;
    }
}
