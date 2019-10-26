package de.invesdwin.util.concurrent.taskinfo.provider;

import de.invesdwin.util.math.decimal.scaled.Percent;

public interface ITaskInfoProvider {

    String getName();

    String getDescription();

    TaskInfoStatus getStatus();

    Percent getProgress();

    boolean isIneritable();

}
