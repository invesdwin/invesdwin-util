package de.invesdwin.util.concurrent.taskinfo;

public interface ITaskInfoListener {

    ITaskInfoListener[] EMPTY_ARRAY = new ITaskInfoListener[0];

    void onTaskInfoAdded(String name);

    void onTaskInfoRemoved(String name);

}
