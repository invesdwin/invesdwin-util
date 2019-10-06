package de.invesdwin.util.concurrent.taskinfo.provider;

import java.lang.ref.WeakReference;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.lang.Objects;

@ThreadSafe
public class WeakReferenceTaskInfoProvider implements ITaskInfoProvider {

    private final String name;
    private final WeakReference<ITaskInfoProvider> reference;
    private final int identityHashCode;

    public WeakReferenceTaskInfoProvider(final ITaskInfoProvider referent) {
        this.name = referent.getName();
        this.reference = new WeakReference<ITaskInfoProvider>(referent);
        this.identityHashCode = System.identityHashCode(referent);
    }

    @Override
    public String getName() {
        return name;
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

}
