package de.invesdwin.util.concurrent.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 */
@ThreadSafe
public final class WaitForGraph {

    private static final Map<Thread, Set<String>> WAIT_ON = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final Map<String, Thread> PIN_OWNER = ILockCollectionFactory.getInstance(true).newConcurrentMap();
    private static final Map<String, Thread> LOCK_OWNER = ILockCollectionFactory.getInstance(true).newConcurrentMap();
    private static final Map<String, Thread> TASK_RUNNING = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final Map<String, Thread> TASK_SUBMITTER = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();
    private static final Map<String, Set<Thread>> JOIN_WAITER = ILockCollectionFactory.getInstance(true).newConcurrentMap();
    //bounded-queue edges: a producer blocked on a full queue only makes progress when the queue's consumer drains it
    private static final Map<String, Thread> QUEUE_CONSUMER = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();

    private WaitForGraph() {}

    public static String newKey(final String tableName, final String keyStr) {
        return String.valueOf(tableName) + "|" + String.valueOf(keyStr);
    }

    public static void beginWait(final String key) {
        WAIT_ON.computeIfAbsent(Thread.currentThread(),
                t -> ILockCollectionFactory.getInstance(true).newConcurrentSet()).add(key);
    }

    public static void endWait(final String key) {
        final Set<String> waits = WAIT_ON.get(Thread.currentThread());
        if (waits != null) {
            waits.remove(key);
            if (waits.isEmpty()) {
                WAIT_ON.remove(Thread.currentThread(), waits);
            }
        }
    }

    public static void beginJoinWait(final String key) {
        JOIN_WAITER.computeIfAbsent(key,
                k -> ILockCollectionFactory.getInstance(true).newConcurrentSet()).add(Thread.currentThread());
        beginWait(key);
    }

    public static void endJoinWait(final String key) {
        final Set<Thread> joiners = JOIN_WAITER.get(key);
        if (joiners != null) {
            joiners.remove(Thread.currentThread());
            if (joiners.isEmpty()) {
                JOIN_WAITER.remove(key, joiners);
            }
        }
        endWait(key);
    }

    /**
     * True when the wait on the given key is performed BY the task serving that key itself (the task pinned the key
     * at acquisition and its nested work demands the same key again): the wait edge is the task's own registration,
     * not a foreign cycle - the task adopts the in-progress state and the joiner keeps waiting for the task normally.
     */
    public static boolean isSelfServedKey(final String key) {
        return Thread.currentThread().equals(TASK_RUNNING.get(key))
                || Thread.currentThread().equals(PIN_OWNER.get(key));
    }

    /**
     * True when the current thread joined (but did not submit) the running task for the key: a pure joiner may
     * continue on the last published store state instead of unwinding (the task's submitter still drives it), while
     * the submitter itself must unwind to release the pins the task chains back onto.
     */
    public static boolean isPureJoinerOf(final String key) {
        final Set<Thread> joiners = JOIN_WAITER.get(key);
        if (joiners == null || !joiners.contains(Thread.currentThread())) {
            return false;
        }
        final String serviceKey = serviceKeyOf(Thread.currentThread());
        final Thread submitter = serviceKey != null ? TASK_SUBMITTER.get(serviceKey) : null;
        final Thread task = TASK_RUNNING.get(key);
        return task != null && task != Thread.currentThread() && submitter != Thread.currentThread();
    }

    public static void lockAcquired(final String key) {
        LOCK_OWNER.put(key, Thread.currentThread());
    }

    public static void lockReleased(final String key) {
        LOCK_OWNER.remove(key, Thread.currentThread());
    }

    public static void taskStarted(final String key, final Thread submitter) {
        TASK_RUNNING.put(key, Thread.currentThread());
        TASK_SUBMITTER.put(key, submitter);
    }

    public static void taskFinished(final String key) {
        TASK_RUNNING.remove(key, Thread.currentThread());
        TASK_SUBMITTER.remove(key);
    }

    public static void pinAcquired(final String key) {
        PIN_OWNER.put(key, Thread.currentThread());
    }

    /**
     * Releases a pin; may be called from another thread (warning-finalizer path), hence the explicit owner.
     */
    public static void pinReleased(final String key, final Thread owner) {
        if (owner != null) {
            PIN_OWNER.remove(key, owner);
        }
    }

    public static void registerQueueConsumer(final String queueKey) {
        QUEUE_CONSUMER.put(queueKey, Thread.currentThread());
    }

    public static void unregisterQueueConsumer(final String queueKey) {
        QUEUE_CONSUMER.remove(queueKey, Thread.currentThread());
    }

    public static String serviceKeyOf(final Thread taskThread) {
        for (final Map.Entry<String, Thread> entry : TASK_RUNNING.entrySet()) {
            if (entry.getValue() == taskThread) {
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
        return walkThread(PIN_OWNER.get(key), self, visitedKeys, visitedThreads, 0, true)
                || walkThread(LOCK_OWNER.get(key), self, visitedKeys, visitedThreads, 0, true)
                || walkThread(TASK_RUNNING.get(key), self, visitedKeys, visitedThreads, 0, true)
                || walkThread(QUEUE_CONSUMER.get(key), self, visitedKeys, visitedThreads, 0, true);
    }

    private static boolean walkKey(final String key, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final int depth, final boolean root) {
        if (depth > 32 || !visitedKeys.add(key)) {
            return false;
        }
        //backtracking DFS (unmark on unwind): the visited sets prune per-path, so the genuine back-edge to the
        //checker through the target key's owners is not swallowed by the first expansion of that same key
        final boolean found = walkThread(PIN_OWNER.get(key), self, visitedKeys, visitedThreads, depth, root)
                || walkThread(LOCK_OWNER.get(key), self, visitedKeys, visitedThreads, depth, root)
                || walkThread(TASK_RUNNING.get(key), self, visitedKeys, visitedThreads, depth, root)
                || walkThread(QUEUE_CONSUMER.get(key), self, visitedKeys, visitedThreads, depth, root);
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
                if (walkKey(otherKey, self, visitedKeys, visitedThreads, depth + 1, false)) {
                    found = true;
                    break;
                }
            }
        }
        visitedThreads.remove(owner);
        return found;
    }

    /**
     * True when the task serving the given key (directly or transitively) must progress through a resource that this
     * thread itself holds or waits behind in a way that would deadlock our own completion: abandon such a join
     * retryably, unwinding lets the task proceed.
     */
    public static boolean joinWouldDeadlockOwnPins(final String key) {
        final Thread self = Thread.currentThread();
        final Set<String> visitedKeys = ILockCollectionFactory.getInstance(false).newSet();
        final Set<Thread> visitedThreads = ILockCollectionFactory.getInstance(false).newSet();
        return checkOwnerReach(PIN_OWNER.get(key), self, visitedKeys, visitedThreads, 1)
                || checkOwnerReach(LOCK_OWNER.get(key), self, visitedKeys, visitedThreads, 1)
                || checkOwnerReach(TASK_RUNNING.get(key), self, visitedKeys, visitedThreads, 1)
                || checkOwnerReach(QUEUE_CONSUMER.get(key), self, visitedKeys, visitedThreads, 1);
    }

    private static boolean walkForSelfPin(final String key, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final int depth) {
        if (depth > 32 || !visitedKeys.add(key)) {
            return false;
        }
        //self-edges at depth 0 are the checker's own service registrations, not a deadlock: expand, do not terminate
        final boolean found = checkOwnerReach(PIN_OWNER.get(key), self, visitedKeys, visitedThreads, depth)
                || checkOwnerReach(LOCK_OWNER.get(key), self, visitedKeys, visitedThreads, depth)
                || checkOwnerReach(TASK_RUNNING.get(key), self, visitedKeys, visitedThreads, depth)
                || checkOwnerReach(QUEUE_CONSUMER.get(key), self, visitedKeys, visitedThreads, depth);
        visitedKeys.remove(key);
        return found;
    }

    private static boolean checkOwnerReach(final Thread owner, final Thread self, final Set<String> visitedKeys,
            final Set<Thread> visitedThreads, final int depth) {
        if (owner == null) {
            return false;
        }
        if (owner == self) {
            return true;
        }
        if (!visitedThreads.add(owner)) {
            return false;
        }
        boolean found = false;
        final Set<String> waits = WAIT_ON.get(owner);
        if (waits != null) {
            for (final String otherKey : ILockCollectionFactory.getInstance(false).newArrayList(waits)) {
                if (walkForSelfPin(otherKey, self, visitedKeys, visitedThreads, depth + 1)) {
                    found = true;
                    break;
                }
            }
        }
        visitedThreads.remove(owner);
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
                    final Thread submitter = TASK_SUBMITTER.get(serviceKey);
                    if (submitter != null && chain.add(submitter)) {
                        grew = true;
                    }
                    final Set<Thread> joiners = JOIN_WAITER.get(serviceKey);
                    if (joiners != null) {
                        for (final Thread joiner : ILockCollectionFactory.getInstance(false).newArrayList(joiners)) {
                            if (chain.add(joiner)) {
                                grew = true;
                            }
                        }
                    }
                }
                final Set<String> waits = WAIT_ON.get(member);
                if (waits != null) {
                    for (final String waitKey : ILockCollectionFactory.getInstance(false).newArrayList(waits)) {
                        for (final Thread owner : ownersOf(waitKey)) {
                            if (chain.add(owner)) {
                                grew = true;
                            }
                        }
                    }
                }
            }
        }
        //the cycle is inside our own demand tree. Adopting (skipping this nested update) is only sound when the pinned
        //version guards the very resource our tree is consuming: either a pin held by a tree member (a completed store
        //version being iterated), or the target key is itself the task we are serving - nested self-demand on our own
        //in-progress key can never be satisfied before we finish, so the current version is the consistent one
        return containsPinOwnerAmong(chain) || isOwnServiceKey(key);
    }

    private static boolean isOwnServiceKey(final String key) {
        final Thread self = Thread.currentThread();
        return self.equals(TASK_RUNNING.get(key)) || self.equals(LOCK_OWNER.get(key));
    }

    private static List<Thread> ownersOf(final String key) {
        final List<Thread> owners = new ArrayList<>(4);
        addOwner(owners, PIN_OWNER.get(key));
        addOwner(owners, LOCK_OWNER.get(key));
        addOwner(owners, TASK_RUNNING.get(key));
        addOwner(owners, QUEUE_CONSUMER.get(key));
        return owners;
    }

    private static void addOwner(final List<Thread> owners, final Thread owner) {
        if (owner != null) {
            owners.add(owner);
        }
    }

    private static boolean containsPinOwnerAmong(final Set<Thread> chain) {
        for (final Thread candidate : chain) {
            for (final Map.Entry<String, Thread> entry : PIN_OWNER.entrySet()) {
                if (entry.getValue() == candidate) {
                    return true;
                }
            }
        }
        return false;
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
        describeThread("pin", PIN_OWNER.get(key), self, visitedKeys, visitedThreads, sb);
        describeThread("lock", LOCK_OWNER.get(key), self, visitedKeys, visitedThreads, sb);
        describeThread("task", TASK_RUNNING.get(key), self, visitedKeys, visitedThreads, sb);
        describeThread("queue", QUEUE_CONSUMER.get(key), self, visitedKeys, visitedThreads, sb);
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
