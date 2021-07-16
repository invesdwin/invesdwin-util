package de.invesdwin.util.math.expression.tokenizer.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AObjectPool;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;

@NotThreadSafe
public final class TokenizerObjectPool extends AObjectPool<Tokenizer> {

    private final List<Tokenizer> tokenizerRotation = new ArrayList<Tokenizer>();

    public TokenizerObjectPool() {
        super(new TokenizerPoolableObjectFactory());
    }

    @Override
    protected Tokenizer internalBorrowObject() {
        if (tokenizerRotation.isEmpty()) {
            return factory.makeObject();
        }
        final Tokenizer tokenizer = tokenizerRotation.remove(0);
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
            removed.add(tokenizerRotation.remove(0));
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
        tokenizerRotation.remove(obj);
    }

}
