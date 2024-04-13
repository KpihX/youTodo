package com.kpihx_lab.youtodo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TacheTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tache getTacheSample1() {
        return new Tache().id(1L).titre("titre1");
    }

    public static Tache getTacheSample2() {
        return new Tache().id(2L).titre("titre2");
    }

    public static Tache getTacheRandomSampleGenerator() {
        return new Tache().id(longCount.incrementAndGet()).titre(UUID.randomUUID().toString());
    }
}
