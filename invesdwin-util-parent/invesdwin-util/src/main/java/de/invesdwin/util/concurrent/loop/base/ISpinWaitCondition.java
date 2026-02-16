package de.invesdwin.util.concurrent.loop.base;

public interface ISpinWaitCondition {

    boolean isConditionFulfilled() throws Exception;

}
