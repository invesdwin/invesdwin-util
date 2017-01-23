package de.invesdwin.util.math.decimal.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.IDecimalAggregate;
import de.invesdwin.util.math.decimal.config.BlockBootstrapConfig;
import de.invesdwin.util.math.decimal.internal.resample.CaseReplacementResampler;
import de.invesdwin.util.math.decimal.internal.resample.CaseResampler;
import de.invesdwin.util.math.decimal.internal.resample.IDecimalResampler;
import de.invesdwin.util.math.decimal.internal.resample.MovingBlockResampler;
import de.invesdwin.util.math.decimal.internal.resample.StationaryBlockResampler;

@ThreadSafe
public class DecimalAggregateBootstraps<E extends ADecimal<E>> {

    private final DecimalAggregate<E> parent;

    private final ALoadingCache<BlockBootstrapConfig, MovingBlockResampler<E>> movingBlockResampler = new ALoadingCache<BlockBootstrapConfig, MovingBlockResampler<E>>() {
        @Override
        protected MovingBlockResampler<E> loadValue(final BlockBootstrapConfig key) {
            return new MovingBlockResampler<E>(parent, key);
        }

        @Override
        protected Integer getMaximumSize() {
            return 10;
        }
    };
    private final ALoadingCache<BlockBootstrapConfig, StationaryBlockResampler<E>> config_stationaryBlockResampler = new ALoadingCache<BlockBootstrapConfig, StationaryBlockResampler<E>>() {
        @Override
        protected StationaryBlockResampler<E> loadValue(final BlockBootstrapConfig key) {
            return new StationaryBlockResampler<E>(parent, key);
        }

        @Override
        protected Integer getMaximumSize() {
            return 10;
        }
    };
    private IDecimalResampler<E> caseResampler;
    private CaseReplacementResampler<E> caseReplacementResampler;

    public DecimalAggregateBootstraps(final DecimalAggregate<E> parent) {
        this.parent = parent;
    }

    public IDecimalAggregate<E> randomize() {
        if (caseResampler == null) {
            caseResampler = new CaseResampler<E>(parent);
        }
        return caseResampler.resample();
    }

    public IDecimalAggregate<E> randomizeBootstrap() {
        if (caseReplacementResampler == null) {
            caseReplacementResampler = new CaseReplacementResampler<E>(parent);
        }
        return caseReplacementResampler.resample();
    }

    public IDecimalAggregate<E> randomizeMovingBootstrap(final BlockBootstrapConfig config) {
        return movingBlockResampler.get(config).resample();
    }

    public IDecimalAggregate<E> randomizeStationaryBootstrap(final BlockBootstrapConfig config) {
        return config_stationaryBlockResampler.get(config).resample();
    }

}
