package de.invesdwin.util.concurrent.taskinfo.provider;

import de.invesdwin.util.math.decimal.scaled.Percent;

public interface ITaskInfoProvider {

    String getName();

    String getDescription();

    TaskInfoStatus getStatus();

    Percent getProgress();

    boolean isIneritable();

    default int getCreatedCount() {
        if (getStatus() == TaskInfoStatus.CREATED) {
            return 1;
        } else {
            return 0;
        }
    }

    default int getStartedCount() {
        if (getStatus() == TaskInfoStatus.STARTED) {
            return 1;
        } else {
            return 0;
        }
    }

    default int getCompletedCount() {
        if (getStatus() == TaskInfoStatus.COMPLETED) {
            return 1;
        } else {
            return 0;
        }
    }

}
