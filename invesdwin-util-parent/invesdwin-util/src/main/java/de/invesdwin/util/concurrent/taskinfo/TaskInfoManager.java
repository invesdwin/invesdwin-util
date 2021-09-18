package de.invesdwin.util.concurrent.taskinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import de.invesdwin.util.lang.Strings;
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
    @GuardedBy("this.class")
    private static final IFastIterableSet<ITaskInfoListener> LISTENERS = ILockCollectionFactory.getInstance(false)
            .newFastIterableLinkedSet();
    private static final FastThreadLocal<Stack<WeakReferenceTaskInfoProvider>> CURRENT_THREAD_TASK_INFO_NAME = new FastThreadLocal<>();
    private static final int MAX_DESCRIPTIONS = 3;

    private TaskInfoManager() {
    }

    public static synchronized void onCreated(final ITaskInfoProvider taskInfoProvider) {
        final String name = taskInfoProvider.getName();
        Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        boolean added = false;
        if (tasks == null) {
            tasks = new LinkedHashMap<>();
            Assertions.checkNull(NAME_TASKS.put(name, tasks));
            added = true;
        } else {
            final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
            if (taskInfo.isCompleted()) {
                tasks.clear();
            }
        }
        final int identityHashCode = System.identityHashCode(taskInfoProvider);
        final WeakReferenceTaskInfoProvider weakReferenceTaskInfoProvider = new WeakReferenceTaskInfoProvider(
                identityHashCode, taskInfoProvider);
        added = tasks.putIfAbsent(identityHashCode, weakReferenceTaskInfoProvider) == null && added;
        if (added) {
            triggerOnTaskInfoAdded(name);
        }
    }

    public static void onStarted(final ITaskInfoProvider taskInfoProvider) {
        if (taskInfoProvider.isIneritable()) {
            Stack<WeakReferenceTaskInfoProvider> taskInfoNameList = CURRENT_THREAD_TASK_INFO_NAME.get();
            if (taskInfoNameList == null) {
                taskInfoNameList = new Stack<WeakReferenceTaskInfoProvider>();
                CURRENT_THREAD_TASK_INFO_NAME.set(taskInfoNameList);
            }
            final int identityHashCode = System.identityHashCode(taskInfoProvider);
            taskInfoNameList.push(new WeakReferenceTaskInfoProvider(identityHashCode, taskInfoProvider));
        }
    }

    private static void triggerOnTaskInfoAdded(final String name) {
        final ITaskInfoListener[] array = LISTENERS.asArray(ITaskInfoListener.EMPTY_ARRAY);
        for (int i = 0; i < array.length; i++) {
            array[i].onTaskInfoAdded(name);
        }
    }

    private static void triggerOnTaskInfoRemoved(final String name) {
        final ITaskInfoListener[] array = LISTENERS.asArray(ITaskInfoListener.EMPTY_ARRAY);
        for (int i = 0; i < array.length; i++) {
            array[i].onTaskInfoRemoved(name);
        }
    }

    public static synchronized void onCompleted(final ITaskInfoProvider taskInfoProvider) {
        if (taskInfoProvider.isIneritable()) {
            final Stack<WeakReferenceTaskInfoProvider> taskInfoNameList = CURRENT_THREAD_TASK_INFO_NAME.get();
            while (taskInfoNameList != null && !taskInfoNameList.isEmpty()) {
                final WeakReferenceTaskInfoProvider peek = taskInfoNameList.peek();
                if (peek.equals(taskInfoProvider) || peek.getStatus() == TaskInfoStatus.COMPLETED) {
                    taskInfoNameList.pop();
                } else {
                    break;
                }
            }
        }
        final String name = taskInfoProvider.getName();
        final Map<Integer, WeakReferenceTaskInfoProvider> tasks = NAME_TASKS.get(name);
        if (tasks != null) {
            final TaskInfo taskInfo = getTaskInfo(name, tasks.values());
            if (taskInfo.isCompleted()) {
                if (taskInfoProvider.isIneritable()) {
                    CURRENT_THREAD_TASK_INFO_NAME.remove();
                }
                Assertions.checkSame(NAME_TASKS.remove(name), tasks);
                triggerOnTaskInfoRemoved(name);
            }
        } else {
            if (taskInfoProvider.isIneritable()) {
                CURRENT_THREAD_TASK_INFO_NAME.remove();
            }
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

    //CHECKSTYLE:OFF
    private static TaskInfo getTaskInfo(final String name, final Collection<WeakReferenceTaskInfoProvider> tasks) {
        //CHECKSTYLE:ON
        int createdCount = 0;
        int startedCount = 0;
        int completedCount = 0;
        double sumProgressRate = 0;
        int progressCount = 0;
        Set<String> createdDescriptions = null;
        Set<String> startedDescriptions = null;
        for (final WeakReferenceTaskInfoProvider task : tasks) {
            final TaskInfoStatus status = task.getStatus();
            createdCount += task.getCreatedCount();
            startedCount += task.getStartedCount();
            completedCount += task.getCompletedCount();
            progressCount++;
            if (status == TaskInfoStatus.COMPLETED) {
                sumProgressRate += 1D;
            } else {
                final Percent progress = task.getProgress();
                if (progress != null) {
                    sumProgressRate += progress.getRate();
                }
            }
            //created descriptions are a fallback if there are no started descriptions
            if (startedDescriptions == null && status == TaskInfoStatus.CREATED
                    && (createdDescriptions == null || createdDescriptions.size() < MAX_DESCRIPTIONS)) {
                final String description = task.getDescription();
                if (Strings.isNotBlank(description)) {
                    if (createdDescriptions == null) {
                        createdDescriptions = new LinkedHashSet<>();
                    }
                    createdDescriptions.add(description);
                }
            }
            if (status == TaskInfoStatus.STARTED
                    && (startedDescriptions == null || startedDescriptions.size() < MAX_DESCRIPTIONS)) {
                final String description = task.getDescription();
                if (Strings.isNotBlank(description)) {
                    if (startedDescriptions == null) {
                        startedDescriptions = new LinkedHashSet<>();
                        createdDescriptions = null;
                    }
                    startedDescriptions.add(description);
                }
            }
        }
        final Set<String> descriptions;
        if (startedDescriptions != null) {
            descriptions = startedDescriptions;
        } else if (createdDescriptions != null) {
            descriptions = createdDescriptions;
        } else {
            descriptions = Collections.emptySet();
        }
        final Percent progress = new Percent(sumProgressRate, progressCount);
        final int tasksCount = createdCount + startedCount + completedCount;
        return new TaskInfo(name, createdCount, startedCount, completedCount, tasksCount, progress, descriptions);
    }

    public static synchronized boolean registerListener(final ITaskInfoListener l) {
        if (LISTENERS.add(l)) {
            for (final String name : getTaskInfoNames()) {
                l.onTaskInfoAdded(name);
            }
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean unregisterListener(final ITaskInfoListener l) {
        return LISTENERS.remove(l);
    }
}
