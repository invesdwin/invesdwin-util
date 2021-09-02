package de.invesdwin.util.math.expression.tokenizer.pool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.pool.commons.ICommonsPoolableObjectFactory;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;

@Immutable
public final class TokenizerPoolableObjectFactory implements ICommonsPoolableObjectFactory<Tokenizer> {

    @Override
    public Tokenizer makeObject() {
        return new Tokenizer();
    }

    @Override
    public void destroyObject(final Tokenizer obj) {
    }

    @Override
    public boolean validateObject(final Tokenizer obj) {
        return true;
    }

    @Override
    public void activateObject(final Tokenizer obj) {
    }

    @Override
    public void passivateObject(final Tokenizer obj) {

    }

}
