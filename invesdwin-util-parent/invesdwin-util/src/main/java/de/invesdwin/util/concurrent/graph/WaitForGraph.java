package de.invesdwin.util.concurrent.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

/**
 * Per-JVM wait-for graph for chains of blocking update/production contexts; implements deterministic deadlock
 * avoidance: before a thread adds a blocking wait edge (parking on a lock, joining an async result, patiently waiting
 * for a heartbeat pin, or a queue producer waiting for its consumer to drain) the graph is walked from the target
 * resource - if the walk leads back to the waiting thread through resources it owns, the wait would close a cycle and
 * must be refused or redirected instead of hanging forever.
 *
 * Keys are opaque identity strings chosen by the callers (for example 'table|key' or 'queue:name@id'); cross-process
 * waits are treated as cycle-free leaves by construction (this graph only knows this JVM's owners).
 *
 * Memory: one entry per active resource (a single KeyState value object holding all owner edges - created on first
 * use, removed as soon as the last edge is released) and one entry per thread currently waiting (entries are dropped
 * on the last endWait of that thread, and dead threads are pruned periodically on mutation). Nothing accumulates for
 * resources or threads that are gone.
 */
@ThreadSafe
public final class WaitForGraph {

    private static final Map<String, KeyState> RESOURCES = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final Map<Thread, Set<String>> WAIT_ON = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final AtomicLong MUTATIONS = new AtomicLong();
    private static final int PRUNE_INTERVAL = 256;

    private WaitForGraph() {}

    private static final class KeyState {
        //null means 'no owner for this edge'; joiners set empty means 'nobody waiting on completion'
        volatile Thread pinOwner;
        volatile Thread lockOwner;
        volatile Thread taskThread;
        volatile Thread taskSubmitter;
        volatile Thread queueConsumer;
        final Set<Thread> joiners = ILockCollectionFactory.getInstance(true).newConcurrentSet();

        boolean isEmpty() {
            return pinOwner == null && lockOwner == null && taskThread == null && queueConsumer == null
                    && joiners.isEmpty();
        }
    }

    private interface KeyMutator {
        void apply(KeyState state);
    }

    /**
     * Atomic per-key mutation; deletes the entry when the state became empty (prevents accumulation of stale keys
     * over the lifetime of a process).
     */
    private static void mutate(final String key, final boolean createIfAbsent, final KeyMutator mutator) {
        RESOURCES.compute(key, (k, state) -> {
            if (state == null) {
                if (!createIfAbsent) {
                    return null;
                }
                state = new KeyState();
            }
            mutator.apply(state);
            return state.isEmpty() ? null : state;
        });
        if (MUTATIONS.incrementAndGet() % PRUNE_INTERVAL == 0) {
            WAIT_ON.entrySet().removeIf(e -> e.getKey().isAlive() ? e.getValue().isEmpty() : true);
        }
    }

    public static String newKey(final String tableName, final String keyStr) {
        return String.valueOf(tableName) + "|" + String.valueOf(keyStr);
    }

    public static void beginWait(final String key) {
        WAIT_ON.computeIfAbsent(Thread.currentThread(),
                t -> ILockCollectionFactory.getInstance(true).newConcurrentSet()).add(key);
    }

    public static void endWait(final String key) {
        final Thread self = Thread.currentThread();
        final Set<String> waits = WAIT_ON.get(self);
        if (waits != null) {
            waits.remove(key);
            if (waits.isEmpty()) {
                WAIT_ON.remove(self, waits);
            }
        }
    }

    public static void beginJoinWait(final String key) {
        mutate(key, true, state -> state.joiners.add(Thread.currentThread()));
        beginWait(key);
    }

    public static void endJoinWait(final String key) {
        mutate(key, false, state -> state.joiners.remove(Thread.currentThread()));
        endWait(key);
    }

    public static void lockAcquired(final String key) {
        mutate(key, true, state -> state.lockOwner = Thread.currentThread());
    }

    public static void lockReleased(final String key) {
        mutate(key, false, state -> state.lockOwner = null);
    }

    public static void taskStarted(final String key, final Thread submitter) {
        mutate(key, true, state -> {
            state.taskThread = Thread.currentThread();
            state.taskSubmitter = submitter;
        });
    }

    public static void taskFinished(final String key) {
        mutate(key, false, state -> {
            state.taskThread = null;
            state.taskSubmitter = null;
        });
    }

    public static void pinAcquired(final String key) {
        mutate(key, true, state -> state.pinOwner = Thread.currentThread());
    }

    /**
     * Releases a pin; may be called from another thread (warning-finalizer path), hence the explicit owner.
     */
    public static void pinReleased(final String key, final Thread owner) {
        mutate(key, false, state -> {
            if (owner != null && owner.equals(state.pinOwner)) {
                state.pinOwner = null;
            }
        });
    }

    public static void registerQueueConsumer(final String queueKey) {
        mutate(queueKey, true, state -> state.queueConsumer = Thread.currentThread());
    }

    public static void unregisterQueueConsumer(final String queueKey) {
        mutate(queueKey, false, state -> state.queueConsumer = null);
    }

    public static String serviceKeyOf(final Thread taskThread) {
        for (final Map.Entry<String, KeyState> entry : RESOURCES.entrySet()) {
            if (taskThread.equals(entry.getValue().taskThread)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * True when the current thread blocking on the given key would close a wait-for cycle within this JVM.
     */
    public static boolean wouldCloseCycle(final String key) {
        final Thread self = Thread.currentThread();
        //Thread has no value-equality, so plain hash sets are exactly identity sets here
        final Set<String> visitedKeys = ILockCollectionFactory.getInstance(false).newSet();
        final Set<Thread> visitedThreads = ILockCollectionFactory.getInstance(false).newSet();
        //root expansion: the target key's owners are evaluated WITHOUT marking the target key itself visited, since
        //the genuine back-edge IS the target key; the checker's own registrations as task/pin/lock owner of the target
        //are not back-edges at root (a thread cannot deadlock on resources it itself serves - locks would be reentrant)
        final KeyState state = RESOURCES.get(key);
        if (state == null) {
            return false;
        }
        return walkThread(state.pinOwner, self, visitedKeys, visitedThreads, 0, true)
                || walkThread(state.lockOwner, self, visitedKeys, visitedThreads, 0, true)
                || walkThread(state.taskThread, self, visitedKeys, visitedThreads, 0, true)
                || walkThread(state.queueConsumer, self, visitedKeys, visitedThreads, 0, true);
    }

    private static boolean walkKey(final String key, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final int depth) {
        if (depth > 32 || !visitedKeys.add(key)) {
            return false;
        }
        final KeyState state = RESOURCES.get(key);
        boolean found = false;
        if (state != null) {
            found = walkThread(state.pinOwner, self, visitedKeys, visitedThreads, depth, false)
                    || walkThread(state.lockOwner, self, visitedKeys, visitedThreads, depth, false)
                    || walkThread(state.taskThread, self, visitedKeys, visitedThreads, depth, false)
                    || walkThread(state.queueConsumer, self, visitedKeys, visitedThreads, depth, false);
        }
        visitedKeys.remove(key);
        return found;
    }

    private static boolean walkThread(final Thread owner, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final int depth, final boolean root) {
        if (owner == null) {
            return false;
        }
        if (owner == self && !root) {
            return true;
        }
        if (!visitedThreads.add(owner)) {
            return false;
        }
        boolean found = false;
        final Set<String> waits = WAIT_ON.get(owner);
        if (waits != null) {
            for (final String otherKey : ILockCollectionFactory.getInstance(false).newArrayList(waits)) {
                if (walkKey(otherKey, self, visitedKeys, visitedThreads, depth + 1)) {
                    found = true;
                    break;
                }
            }
        }
        visitedThreads.remove(owner);
        return found;
    }

    public static boolean isSelfServedKey(final String key) {
        final KeyState state = RESOURCES.get(key);
        if (state == null) {
            return false;
        }
        final Thread self = Thread.currentThread();
        return self.equals(state.taskThread) || self.equals(state.pinOwner);
    }

    /**
     * True when the task serving the given key chains (directly or transitively) back onto a heartbeat PIN held by
     * the current thread: joining it would deadlock our own open update results closed. Only pin edges are hard:
     * lock, task and queue positions are transient (their holders complete and drain on their own), so they are
     * expanded to search for a pin back-edge but never terminate the search themselves. Without this restriction
     * legitimate long remote pulls would be refused at every join and retry-churn the whole suite.
     */
    public static boolean joinWouldDeadlockOwnPins(final String key) {
        final Thread self = Thread.currentThread();
        final Set<String> visitedKeys = ILockCollectionFactory.getInstance(false).newSet();
        final Set<Thread> visitedThreads = ILockCollectionFactory.getInstance(false).newSet();
        return pinBackEdgeExists(key, self, visitedKeys, visitedThreads, 0);
    }

    private static boolean pinBackEdgeExists(final String key, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final int depth) {
        if (depth > 32 || !visitedKeys.add(key)) {
            return false;
        }
        final KeyState state = RESOURCES.get(key);
        boolean found = false;
        if (state != null) {
            found = state.pinOwner == self;
            if (!found) {
                for (final Thread owner : ownersOf(state)) {
                    if (owner == self || owner == null || !visitedThreads.add(owner)) {
                        continue;
                    }
                    final Set<String> waits = WAIT_ON.get(owner);
                    if (waits != null) {
                        for (final String otherKey : ILockCollectionFactory.getInstance(false).newArrayList(waits)) {
                            if (pinBackEdgeExists(otherKey, self, visitedKeys, visitedThreads, depth + 1)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }
        visitedKeys.remove(key);
        return found;
    }

    /**
     * True when a detected cycle closes inside the current thread's own demand tree: the closure contains this
     * thread, the submitter chain of every task it serves, every thread currently joining those tasks, and the owners
     * of resources those threads already wait for. A heartbeat pin held anywhere inside that closure guards exactly the
     * published-complete store version the tree is consuming, so adopting it (skipping the update for this attempt)
     * is tree-consistent; cycles reaching outside the closure are foreign and must be refused outright.
     */
    public static boolean isCycleOwnedByOwnDemandTree(final String key) {
        if (!wouldCloseCycle(key)) {
            return false;
        }
        final Thread self = Thread.currentThread();
        final Set<Thread> chain = ILockCollectionFactory.getInstance(false).newSet();
        chain.add(self);
        boolean grew = true;
        for (int round = 0; grew && round < 8; round++) {
            grew = false;
            for (final Thread member : ILockCollectionFactory.getInstance(false).newArrayList(chain)) {
                final String serviceKey = serviceKeyOf(member);
                if (serviceKey != null) {
                    final KeyState state = RESOURCES.get(serviceKey);
                    if (state != null) {
                        if (state.taskSubmitter != null && chain.add(state.taskSubmitter)) {
                            grew = true;
                        }
                        for (final Thread joiner : ILockCollectionFactory.getInstance(false)
                                .newArrayList(state.joiners)) {
                            if (chain.add(joiner)) {
                                grew = true;
                            }
                        }
                    }
                }
                final Set<String> waits = WAIT_ON.get(member);
                if (waits != null) {
                    for (final String waitKey : ILockCollectionFactory.getInstance(false).newArrayList(waits)) {
                        final KeyState waitState = RESOURCES.get(waitKey);
                        if (waitState != null) {
                            for (final Thread owner : ownersOf(waitState)) {
                                if (chain.add(owner)) {
                                    grew = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (final Map.Entry<String, KeyState> entry : RESOURCES.entrySet()) {
            if (entry.getValue().pinOwner != null && chain.contains(entry.getValue().pinOwner)) {
                return true;
            }
        }
        return false;
    }

    private static List<Thread> ownersOf(final KeyState state) {
        final List<Thread> owners = new ArrayList<>(4);
        addOwner(owners, state.pinOwner);
        addOwner(owners, state.lockOwner);
        addOwner(owners, state.taskThread);
        addOwner(owners, state.queueConsumer);
        return owners;
    }

    private static void addOwner(final List<Thread> owners, final Thread owner) {
        if (owner != null) {
            owners.add(owner);
        }
    }

    public static String describeCycle(final String key) {
        final StringBuilder sb = new StringBuilder("wait-for chain for [").append(shorten(key)).append("]:");
        final Thread self = Thread.currentThread();
        final Set<String> visitedKeys = ILockCollectionFactory.getInstance(false).newSet();
        final Set<Thread> visitedThreads = ILockCollectionFactory.getInstance(false).newSet();
        describeKey(key, self, visitedKeys, visitedThreads, sb);
        return sb.toString();
    }

    private static String shorten(final String key) {
        if (key.length() <= 120) {
            return key;
        }
        return key.substring(0, 100) + "...(" + key.length() + " chars)";
    }

    private static void describeKey(final String key, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final StringBuilder sb) {
        if (!visitedKeys.add(key)) {
            return;
        }
        final KeyState state = RESOURCES.get(key);
        if (state == null) {
            return;
        }
        describeThread("pin", state.pinOwner, self, visitedKeys, visitedThreads, sb);
        describeThread("lock", state.lockOwner, self, visitedKeys, visitedThreads, sb);
        describeThread("task", state.taskThread, self, visitedKeys, visitedThreads, sb);
        describeThread("queue", state.queueConsumer, self, visitedKeys, visitedThreads, sb);
    }

    private static void describeThread(final String role, final Thread owner, final Thread self,
            final Set<String> visitedKeys, final Set<Thread> visitedThreads, final StringBuilder sb) {
        if (owner == null) {
            return;
        }
        sb.append(" ").append(role).append("=[").append(owner.getName()).append("]");
        if (owner == self || !visitedThreads.add(owner)) {
            return;
        }
        final Set<String> waits = WAIT_ON.get(owner);
        if (waits != null) {
            for (final String otherKey : ILockCollectionFactory.getInstance(false).newArrayList(waits)) {
                describeKey(otherKey, self, visitedKeys, visitedThreads, sb);
            }
        }
    }

}
