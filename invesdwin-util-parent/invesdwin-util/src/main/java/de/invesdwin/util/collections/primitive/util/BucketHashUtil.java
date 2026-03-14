package de.invesdwin.util.collections.primitive.util;

import javax.annotation.concurrent.Immutable;

import org.jspecify.annotations.Nullable;

import de.invesdwin.util.lang.Objects;
import it.unimi.dsi.fastutil.HashCommon;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Concurrent Fast Util
 * 
 * @see it.unimi.dsi.fastutil.HashCommon#mix
 * @see it.unimi.dsi.fastutil.HashCommon#murmurHash3
 * @see java.util.Objects#hashCode(Object)
 * @see jdk.internal.util.random.RandomSupport#mixMurmur64
 * @see org.springframework.util.ConcurrentReferenceHashMap#getHash(Object)
 */
@Immutable
public final class BucketHashUtil {
    public static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

    private BucketHashUtil() {}

    /// re-hash 👀 hash4j
    ///
    /// @see it.unimi.dsi.fastutil.HashCommon#murmurHash3
    /// @see org.springframework.util.ConcurrentReferenceHashMap#getHash(Object)
    /// @see org.jsr166.ConcurrentLinkedHashMap#hash(int)
    /// @see jdk.internal.classfile.impl.AbstractPoolEntry#phiMix
    /// @see io.vertx.core.json.jackson.HybridJacksonPool.XorShiftThreadProbe#probe
    /// @see com.google.common.util.concurrent.Striped#smear
    /// @see java.util.concurrent.ThreadLocalRandom#PROBE_INCREMENT
    /// @see java.util.concurrent.ThreadLocalRandom#advanceProbe
    /// @see #bucket
    /// @see #hash
    public static int hash(final int hashOrKey) {
        return HashCommon.murmurHash3(hashOrKey);
    }

    public static int hash(final long hashOrKey) {
        return hashOrKey < Integer.MIN_VALUE || hashOrKey > Integer.MAX_VALUE
                ? Long.hashCode(HashCommon.murmurHash3(hashOrKey))
                : HashCommon.murmurHash3((int) hashOrKey);// if long uses low 32bit only
    }

    public static int hash(@Nullable final Long hashOrKey) {
        if (hashOrKey == null) {
            return 0;
        }
        return hashOrKey < Integer.MIN_VALUE || hashOrKey > Integer.MAX_VALUE
                ? Long.hashCode(HashCommon.murmurHash3(hashOrKey))
                : HashCommon.murmurHash3(hashOrKey.intValue());// if long uses low 32bit only
    }

    public static int hash(@Nullable final Object object4hashCode) {
        if (object4hashCode == null) {
            return 0;
        } else if (object4hashCode instanceof Long) {
            final Long n = (Long) object4hashCode;
            return n < Integer.MIN_VALUE || n > Integer.MAX_VALUE ? Long.hashCode(HashCommon.murmurHash3(n))
                    : HashCommon.murmurHash3(n.intValue());// if long uses low 32bit only
        } else {
            return HashCommon.murmurHash3(object4hashCode.hashCode());
        }
    }

    /** @see #bucket */
    public static @PositiveOrZero int bucket(final int hashOrKey, @Positive final int bucketSize) {
        return Math.abs(hash(hashOrKey) % bucketSize);
    }

    /// @see #bucket
    /// @see java.util.Objects#hashCode(Object)
    public static @PositiveOrZero int bucket(@Nullable final Object object4hashCode, @Positive final int bucketSize) {
        return Math.abs(hash(object4hashCode) % bucketSize);
    }

    /**
     * Get positive, quite `random` bucket index between [0 and bucketSize-1] for any key. Fast. Safe for negative keys
     * (including Long.MIN_VALUE, Integer.MIN_VALUE)
     * 
     * FastUtil has ❌ HashCommon mix/murmurHash3(long), but we use ✅ Long.hashCode + murmurHash3(int) because (long):
     * 
     * mix(1L) ≠ mix(1) → it is against common knowledge and expectations mix(1L) ≠ mix(Long.valueOf(1L)) → 😱
     * 
     * @see #hash(int)
     * @see Long#hashCode(long)
     */
    public static @PositiveOrZero int bucket(final long hashOrKey, @Positive final int bucketSize) {
        return Math.abs(hash(hashOrKey) % bucketSize);
    }

    public static @PositiveOrZero int bucket(@Nullable final Long hashOrKey, @Positive final int bucketSize) {
        return Math.abs(hash(hashOrKey) % bucketSize);
    }

    /**
     * Combined two 32-bit keys into a 64-bit compound.
     * 
     * https://github.com/aeron-io/agrona/blob/master/agrona/src/main/java/org/agrona/collections/Hashing.java
     *
     * @param keyHi
     *            to make the upper bits
     * @param keyLo
     *            to make the lower bits
     * @return the compound key
     */
    public static long compoundKey(final int keyHi, final int keyLo) {
        return ((long) keyHi << 32) | (keyLo & 0xFfFf_FfFfL);
    }

    /**
     * Is varargs empty?
     * 
     * @see #safeVarArgs
     */
    public static boolean blankVarargs(@Nullable final Object @Nullable [] args) {
        return args == null || args.length == 0 || (args.length == 1 && args[0] == null);
    }

    /// Fix usual varargs mistakes (but type is lost) ~ Considers an Object array passed into a varargs parameter as
    /// collection of arguments rather than as single argument).
    ///
    /// @see #blankVarargs
    public static Object @Nullable [] safeVarArgs(@Nullable final Object @Nullable [] varArgs) {
        if (varArgs == null) {
            return Objects.EMPTY_ARRAY;// not some T[]!
        }

        if (varArgs.length == 1 && varArgs[0] instanceof Object[]) { // antT[] instanceof Object[]!
            final Object[] a = (Object[]) varArgs[0];
            return a;
        }

        return varArgs;// usual good varargs
    }
}