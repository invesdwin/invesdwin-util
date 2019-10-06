package de.invesdwin.util.concurrent.taskinfo;

public interface ITaskInfoListener {

    void onTaskInfoAdded(String name);

    void onTaskInfoRemoved(String name);

}
