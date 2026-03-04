package de.invesdwin.util.concurrent.loop.spinwait;

public interface ISpinWaitCondition {

    boolean isConditionFulfilled() throws Exception;

}
