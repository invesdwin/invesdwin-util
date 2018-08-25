package de.invesdwin.util.concurrent.lock;

import java.util.concurrent.locks.Lock;

public interface ILock extends Lock {

    String getName();

}
