package de.invesdwin.util.collections.attributes;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class PrefixedDelegateAttributesMap extends PrefixedDelegateAttributesMapContract implements IAttributesMap {

    protected final IAttributesMap delegate;
    private PrefixedDelegateAttributesMapContract soft;
    private PrefixedDelegateAttributesMapContract weak;

    public PrefixedDelegateAttributesMap(final IAttributesMap delegate, final String prefix) {
        super(delegate, prefix);
        this.delegate = delegate;
    }

    @Override
    public IAttributesMapContract getSoft() {
        if (soft == null) {
            synchronized (this) {
                if (soft == null) {
                    soft = new PrefixedDelegateAttributesMapContract(delegate.getSoft(), prefix);
                }
            }
        }
        return soft;
    }

    @Override
    public IAttributesMapContract getWeak() {
        if (weak == null) {
            synchronized (this) {
                if (weak == null) {
                    weak = new PrefixedDelegateAttributesMapContract(delegate.getWeak(), prefix);
                }
            }
        }
        return weak;
    }

}
