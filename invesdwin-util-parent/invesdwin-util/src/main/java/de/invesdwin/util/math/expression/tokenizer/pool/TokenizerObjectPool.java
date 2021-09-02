package de.invesdwin.util.math.expression.tokenizer.pool;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.concurrent.pool.commons.ACommonsObjectPool;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;

@NotThreadSafe
public final class TokenizerObjectPool extends ACommonsObjectPool<Tokenizer> {

    private final NodeBufferingIterator<Tokenizer> tokenizerRotation = new NodeBufferingIterator<Tokenizer>();

    public TokenizerObjectPool() {
        super(new TokenizerPoolableObjectFactory());
    }

    @Override
    protected Tokenizer internalBorrowObject() {
        if (tokenizerRotation.isEmpty()) {
            return factory.makeObject();
        }
        final Tokenizer tokenizer = tokenizerRotation.next();
        if (tokenizer != null) {
            return tokenizer;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public int getNumIdle() {
        return tokenizerRotation.size();
    }

    @Override
    public Collection<Tokenizer> internalClear() {
        final Collection<Tokenizer> removed = new ArrayList<Tokenizer>();
        while (!tokenizerRotation.isEmpty()) {
            removed.add(tokenizerRotation.next());
        }
        return removed;
    }

    @Override
    protected Tokenizer internalAddObject() {
        final Tokenizer pooled = factory.makeObject();
        tokenizerRotation.add(factory.makeObject());
        return pooled;
    }

    @Override
    protected void internalReturnObject(final Tokenizer obj) {
        tokenizerRotation.add(obj);
    }

    @Override
    protected void internalInvalidateObject(final Tokenizer obj) {
        //Nothing happens
    }

    @Override
    protected void internalRemoveObject(final Tokenizer obj) {
        tokenizerRotation.next();
    }

}
