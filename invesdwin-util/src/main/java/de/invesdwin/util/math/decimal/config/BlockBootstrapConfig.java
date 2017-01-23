package de.invesdwin.util.math.decimal.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.bean.AValueObject;

@NotThreadSafe
public class BlockBootstrapConfig extends AValueObject {

    private Integer blockLength = null;

    /**
     * If this is null, an optimal block length will be determined.
     */
    public BlockBootstrapConfig withBlockLength(final Integer blockLength) {
        this.blockLength = blockLength;
        return this;
    }

    public Integer getBlockLength() {
        return blockLength;
    }

}
