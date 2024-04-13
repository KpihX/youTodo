package com.kpihx_lab.youtodo.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CategorieTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Categorie getCategorieSample1() {
        return new Categorie().id(1L).nom("nom1");
    }

    public static Categorie getCategorieSample2() {
        return new Categorie().id(2L).nom("nom2");
    }

    public static Categorie getCategorieRandomSampleGenerator() {
        return new Categorie().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString());
    }
}
