package de.invesdwin.util.concurrent.taskinfo.provider;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum TaskInfoStatus {
    CREATED,
    STARTED,
    COMPLETED;
}
