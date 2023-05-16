package de.invesdwin.util.time.duration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@NotThreadSafe
public abstract class AEstimatedRemainingDuration extends AValueObject {

    public static final Duration UNKNOWN_DURATION = Duration.ZERO;
    private static final double RESET_ON_FASTER_ESTIMATIONS_THRESHOLD = 1.25D;
    private static final int MAX_HISTORY_COUNT = 60;
    private static final int UNSURENESS_LOOKBACK_COUNT = 10;
    private Percent firstProgressPercent;
    private final List<Duration> previousEstimatedFullDurations = new ArrayList<Duration>();
    private final List<FDate> previousEstimatedFullDurationsUpdateTimes = new ArrayList<FDate>();
    private final List<Percent> previousEstimatedFullDurationProgressPercents = new ArrayList<Percent>();
    private final List<Percent> previousUnsurenessMultiplicators = new ArrayList<Percent>();
    private FDate lastUpdate = FDates.MIN_DATE;
    private Duration maxEstimatedFullDuration = null;

    public Duration getEstimatedRemainingDuration() {
        final Percent newProgressPercent = getProgressPercent();
        if (newProgressPercent == null) {
            return UNKNOWN_DURATION;
        }
        if (firstProgressPercent == null) {
            firstProgressPercent = newProgressPercent;
        }
        final Percent progressPercent = Percent.normalize(newProgressPercent, firstProgressPercent,
                Percent.ONE_HUNDRED_PERCENT);
        if (progressPercent.isGreaterThan(Percent.ZERO_PERCENT)) {
            final Duration fullDuration = getEstimatedFullDuration(progressPercent);
            final Duration remainingDuration = fullDuration
                    .multiply(Percent.ONE_HUNDRED_PERCENT.subtract(progressPercent));
            return remainingDuration;
        } else {
            //uknown duration
            return UNKNOWN_DURATION;
        }
    }

    private Duration getEstimatedFullDuration(final Percent progressPercent) {
        final FDate curTime = new FDate();
        if (new Duration(lastUpdate, curTime).isGreaterThan(Duration.ONE_SECOND)) {
            lastUpdate = curTime;
            final Duration elapsedDuration = getElapsedDuration();
            final Duration fullDuration = elapsedDuration.divide(progressPercent.getRate()).orHigher(Duration.ZERO);
            maybeResetEstimations(fullDuration);
            previousEstimatedFullDurations.add(fullDuration);
            previousEstimatedFullDurationsUpdateTimes.add(curTime);
            previousEstimatedFullDurationProgressPercents.add(progressPercent);
            while (previousEstimatedFullDurations.size() > MAX_HISTORY_COUNT) {
                previousEstimatedFullDurations.remove(0);
                previousEstimatedFullDurationsUpdateTimes.remove(0);
                previousEstimatedFullDurationProgressPercents.remove(0);
            }

            maxEstimatedFullDuration = Duration.valueOf(previousEstimatedFullDurations).avgWeightedAsc();
            final Percent newUnsurenessMultiplicator = determineUnsurenessMultiplicator(progressPercent);
            previousUnsurenessMultiplicators.add(newUnsurenessMultiplicator);
            while (previousUnsurenessMultiplicators.size() > MAX_HISTORY_COUNT) {
                previousUnsurenessMultiplicators.remove(0);
            }
            final Percent unsurenessMultiplicator = Percent.valueOf(previousUnsurenessMultiplicators).avg();
            maxEstimatedFullDuration = maxEstimatedFullDuration.multiply(unsurenessMultiplicator);
        }
        return maxEstimatedFullDuration;
    }

    private void maybeResetEstimations(final Duration fullDuration) {
        if (!previousEstimatedFullDurations.isEmpty()) {
            for (int i = 0; i < previousEstimatedFullDurations.size(); i++) {
                final Duration prevFullDiration = previousEstimatedFullDurations.get(i);
                if (prevFullDiration.isZero() || prevFullDiration
                        .getNumMultipleOfPeriod(fullDuration) >= RESET_ON_FASTER_ESTIMATIONS_THRESHOLD) {
                    //reset estimations
                    previousEstimatedFullDurations.clear();
                    previousEstimatedFullDurationsUpdateTimes.clear();
                    previousEstimatedFullDurationProgressPercents.clear();
                    previousUnsurenessMultiplicators.clear();
                }
            }
        }
    }

    /**
     * We use a multiplier that gets more pessimistic the more unregular the performance is and the further we are away
     * from completion.
     */
    private Percent determineUnsurenessMultiplicator(final Percent progressPercent) {
        final Percent unsurenessMultiplicator;
        if (previousEstimatedFullDurations.size() > 1) {
            final int lastIndex = previousEstimatedFullDurations.size() - 1;
            final int firstIndex = Integers.max(0, lastIndex - UNSURENESS_LOOKBACK_COUNT);
            final Duration firstFullEstimation = previousEstimatedFullDurations.get(firstIndex);
            final Duration lastFullEstimation = previousEstimatedFullDurations.get(lastIndex);
            final FDate firstUpdateTime = previousEstimatedFullDurationsUpdateTimes.get(firstIndex);
            final FDate lastUpdateTime = previousEstimatedFullDurationsUpdateTimes.get(lastIndex);
            final Percent firstProgressPercent = previousEstimatedFullDurationProgressPercents.get(firstIndex);
            final Percent lastProgressPercent = previousEstimatedFullDurationProgressPercents.get(lastIndex);

            final Duration firstRemainingDuration = firstFullEstimation
                    .multiply(Percent.ONE_HUNDRED_PERCENT.subtract(firstProgressPercent));
            final Duration lastRemainingDuration = lastFullEstimation
                    .multiply(Percent.ONE_HUNDRED_PERCENT.subtract(lastProgressPercent));

            //negative is faster than expected; positive is slower than expected
            final Duration durationDiff = lastRemainingDuration.subtract(firstRemainingDuration);
            final Duration remainingEstimationMovement = durationDiff.abs();
            final Duration timeMovement = new Duration(firstUpdateTime, lastUpdateTime);

            final Duration longerDuration = Duration.max(remainingEstimationMovement, timeMovement);
            final Duration shorterDuration = Duration.min(remainingEstimationMovement, timeMovement);
            //limit multiplicator between 100% and 300%
            final Percent maxUnsureness;
            if (durationDiff.isNegative()) {
                maxUnsureness = Percent.TWO_HUNDRED_PERCENT;
            } else {
                maxUnsureness = Percent.THREE_HUNDRED_PERCENT;
            }
            unsurenessMultiplicator = new Percent(longerDuration, shorterDuration).between(Percent.ONE_HUNDRED_PERCENT,
                    maxUnsureness);
        } else {
            unsurenessMultiplicator = Percent.TWO_HUNDRED_PERCENT;
        }
        return adjustUnsurenessMultiplicatorByProgress(unsurenessMultiplicator, progressPercent);
    }

    private Percent adjustUnsurenessMultiplicatorByProgress(final Percent unsurenessMultiplicator,
            final Percent progressPercent) {
        final Percent remainingProgressPercent = Percent.ONE_HUNDRED_PERCENT.subtract(progressPercent);
        final Percent additionalUnsureness = unsurenessMultiplicator.subtract(Percent.ONE_HUNDRED_PERCENT);
        return Percent.ONE_HUNDRED_PERCENT.add(additionalUnsureness.multiply(remainingProgressPercent));
    }

    protected abstract Duration getElapsedDuration();

    protected abstract Percent getProgressPercent();

}
