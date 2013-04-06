package com.airhacks.afterburner.injection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author adam-bien.com
 */
public class Boundary {

    static AtomicInteger INSTANCE_COUNT = new AtomicInteger(0);

    public Boundary() {
        INSTANCE_COUNT.incrementAndGet();
    }

    public int getNumberOfInstances() {
        return INSTANCE_COUNT.get();
    }
}
