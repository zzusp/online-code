package com.onlinecode.admin.redis;

import java.util.Objects;

public class DLock<T> implements AutoCloseable {

    private T lock;
    private DistributedLock<T> distributedLock;

    public DLock() {
    }

    public DLock(T lock, DistributedLock<T> distributedLock) {
        this.lock = lock;
        this.distributedLock = distributedLock;
    }

    public T getLock() {
        return lock;
    }

    public void setLock(T lock) {
        this.lock = lock;
    }

    public DistributedLock<T> getDistributedLock() {
        return distributedLock;
    }

    public void setDistributedLock(DistributedLock<T> distributedLock) {
        this.distributedLock = distributedLock;
    }

    @Override
    public void close() throws Exception {
        if (Objects.nonNull(lock)) {
            distributedLock.unLock(lock);
        }
    }
}
