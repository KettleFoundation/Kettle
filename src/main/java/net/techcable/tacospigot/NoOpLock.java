package net.techcable.tacospigot;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class NoOpLock implements Lock {
    private NoOpLock() {
    }

    public static final NoOpLock INSTANCE = new NoOpLock();

    @Override
    public void lock() {
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
