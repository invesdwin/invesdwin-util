package de.invesdwin.util.math.expression.tokenizer.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AQueueObjectPool;
import de.invesdwin.util.concurrent.pool.AgronaObjectPool;
import de.invesdwin.util.math.expression.tokenizer.Tokenizer;

@ThreadSafe
public final class TokenizerObjectPool extends AQueueObjectPool<Tokenizer> {

    public static final TokenizerObjectPool INSTANCE = new TokenizerObjectPool();

    private TokenizerObjectPool() {
        super(new ManyToManyConcurrentArrayQueue<>(AgronaObjectPool.DEFAULT_MAX_POOL_SIZE));
    }

    @Override
    protected Tokenizer newObject() {
        return new Tokenizer();
    }

}
