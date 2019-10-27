package de.invesdwin.util.concurrent.taskinfo.provider;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.decimal.scaled.Percent;

@ThreadSafe
public class WeakReferenceTaskInfoProvider implements ITaskInfoProvider {

    private final String name;
    private final WeakReference<ITaskInfoProvider> reference;
    private final int identityHashCode;

    public WeakReferenceTaskInfoProvider(final int identityHashCode, final ITaskInfoProvider referent) {
        this.name = referent.getName();
        this.reference = new WeakReference<ITaskInfoProvider>(referent);
        this.identityHashCode = identityHashCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.getDescription();
        } else {
            return null;
        }
    }

    @Override
    public TaskInfoStatus getStatus() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.getStatus();
        } else {
            return TaskInfoStatus.COMPLETED;
        }
    }

    @Override
    public Percent getProgress() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.getProgress();
        } else {
            return Percent.ONE_HUNDRED_PERCENT;
        }
    }

    @Override
    public int hashCode() {
        return identityHashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ITaskInfoProvider && obj.hashCode() == identityHashCode;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name).add("identity", identityHashCode).toString();
    }

    @Override
    public boolean isIneritable() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.isIneritable();
        } else {
            return true;
        }
    }

    public int getCreatedCount() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.getCreatedCount();
        } else {
            return 0;
        }
    }

    public int getStartedCount() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.getStartedCount();
        } else {
            return 0;
        }
    }

    public int getCompletedCount() {
        final ITaskInfoProvider referent = reference.get();
        if (referent != null) {
            return referent.getCompletedCount();
        } else {
            return 1;
        }
    }

}
