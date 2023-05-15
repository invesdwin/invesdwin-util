/*
 * Taken from <a href=
 * "http://grepcode.com/file_/repo1.maven.org/maven2/commons-pool/commons-pool/1.6/org/apache/commons/pool/PoolableObjectFactory.java/?v=source"
 * >Source</a> in order to not have to use PooledObject everywhere without needing it -_-
 */

package de.invesdwin.util.concurrent.pool.commons;

// CHECKSTYLE:OFF unused import
import org.apache.commons.pool2.ObjectPool;

// CHECKSTYLE:ON

/**
 * An interface defining life-cycle methods for instances to be served by an {@link ObjectPool}.
 * <p>
 * By contract, when an {@link ObjectPool} delegates to a {@link PoolableObjectFactory},
 * <ol>
 * <li>{@link #makeObject makeObject} is called whenever a new instance is needed.</li>
 * <li>{@link #activateObject activateObject} is invoked on every instance that has been {@link #passivateObject
 * passivated} before it is {@link ObjectPool#borrowObject borrowed} from the pool.</li>
 * <li>{@link #validateObject validateObject} is invoked on {@link #activateObject activated} instances to make sure
 * they can be {@link ObjectPool#borrowObject borrowed} from the pool. <code>validateObject</code> <strong>may</strong>
 * also be used to test an instance being {@link ObjectPool#returnObject returned} to the pool before it is
 * {@link #passivateObject passivated}. It will only be invoked on an activated instance.</li>
 * <li>{@link #passivateObject passivateObject} is invoked on every instance when it is returned to the pool.</li>
 * <li>{@link #destroyObject destroyObject} is invoked on every instance when it is being "dropped" from the pool
 * (whether due to the response from <code>validateObject</code>, or for reasons specific to the pool implementation.)
 * There is no guarantee that the instance being destroyed will be considered active, passive or in a generally
 * consistent state.</li>
 * </ol>
 * </p>
 * <p>
 * {@link PoolableObjectFactory} must be thread-safe. The only promise an {@link ObjectPool} makes is that the same
 * instance of an object will not be passed to more than one method of a <code>PoolableObjectFactory</code> at a time.
 * </p>
 *
 * @param <T>
 *            the type of objects held in this pool
 * 
 * @see ObjectPool
 *
 * @author Rodney Waldhoff
 * @author Sandy McArthur
 * @version $Revision: 1222388 $ $Date: 2011-12-22 13:28:27 -0500 (Thu, 22 Dec 2011) $
 * @since Pool 1.0
 */
public interface ICommonsPoolableObjectFactory<T> {
    /**
     * Creates an instance that can be served by the pool. Instances returned from this method should be in the same
     * state as if they had been {@link #activateObject activated}. They will not be activated before being served by
     * the pool.
     *
     * @return an instance that can be served by the pool.
     * @throws Exception
     *             if there is a problem creating a new instance, this will be propagated to the code requesting an
     *             object.
     */
    T makeObject();

    /**
     * Destroys an instance no longer needed by the pool.
     * <p>
     * It is important for implementations of this method to be aware that there is no guarantee about what state
     * <code>obj</code> will be in and the implementation should be prepared to handle unexpected errors.
     * </p>
     * <p>
     * Also, an implementation must take in to consideration that instances lost to the garbage collector may never be
     * destroyed.
     * </p>
     *
     * @param obj
     *            the instance to be destroyed
     * @throws Exception
     *             should be avoided as it may be swallowed by the pool implementation.
     * @see #validateObject
     * @see ObjectPool#invalidateObject
     */
    void destroyObject(T obj);

    /**
     * Ensures that the instance is safe to be returned by the pool. Returns <code>false</code> if <code>obj</code>
     * should be destroyed.
     *
     * @param obj
     *            the instance to be validated
     * @return <code>false</code> if <code>obj</code> is not valid and should be dropped from the pool,
     *         <code>true</code> otherwise.
     */
    boolean validateObject(T obj);

    /**
     * Reinitialize an instance to be returned by the pool.
     *
     * @param obj
     *            the instance to be activated
     * @throws Exception
     *             if there is a problem activating <code>obj</code>, this exception may be swallowed by the pool.
     * @see #destroyObject
     */
    void activateObject(T obj);

    /**
     * Uninitialize an instance to be returned to the idle object pool.
     *
     * @param obj
     *            the instance to be passivated
     * @return true if the object can be reused, false if it should be destroyed
     * @throws Exception
     *             if there is a problem passivating <code>obj</code>, this exception may be swallowed by the pool.
     * @see #destroyObject
     */
    boolean passivateObject(T obj);
}
