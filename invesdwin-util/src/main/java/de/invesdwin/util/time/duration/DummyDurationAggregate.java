package de.invesdwin.util.time.duration;

import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
final class DummyDurationAggregate implements IDurationAggregate {

    public static final DummyDurationAggregate INSTANCE = new DummyDurationAggregate();

    private DummyDurationAggregate() {}

    @Override
    public IDurationAggregate reverse() {
        return this;
    }

    @Override
    public Duration sum() {
        return null;
    }

    @Override
    public Duration avg() {
        return null;
    }

    @Override
    public Duration max() {
        return null;
    }

    @Override
    public Duration min() {
        return null;
    }

    @Override
    public List<? extends Duration> values() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public Duration avgWeightedAsc() {
        return null;
    }

    @Override
    public Duration avgWeightedDesc() {
        return null;
    }

}
