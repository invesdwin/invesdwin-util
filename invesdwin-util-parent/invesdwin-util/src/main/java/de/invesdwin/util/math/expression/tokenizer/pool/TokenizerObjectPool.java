package de.invesdwin.util.math.expression.tokenizer.pool;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;

@ThreadSafe
public final class TokenizerObjectPool extends AAgronaObjectPool<Tokenizer> {

    public static final TokenizerObjectPool INSTANCE = new TokenizerObjectPool();

    private TokenizerObjectPool() {
    }

    @Override
    protected Tokenizer newObject() {
        return new Tokenizer();
    }

}
