package com.kpihx_lab.youtodo.domain;

import static com.kpihx_lab.youtodo.domain.CategorieTestSamples.*;
import static com.kpihx_lab.youtodo.domain.TacheTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kpihx_lab.youtodo.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TacheTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tache.class);
        Tache tache1 = getTacheSample1();
        Tache tache2 = new Tache();
        assertThat(tache1).isNotEqualTo(tache2);

        tache2.setId(tache1.getId());
        assertThat(tache1).isEqualTo(tache2);

        tache2 = getTacheSample2();
        assertThat(tache1).isNotEqualTo(tache2);
    }

    @Test
    void categorieTest() throws Exception {
        Tache tache = getTacheRandomSampleGenerator();
        Categorie categorieBack = getCategorieRandomSampleGenerator();

        tache.setCategorie(categorieBack);
        assertThat(tache.getCategorie()).isEqualTo(categorieBack);

        tache.categorie(null);
        assertThat(tache.getCategorie()).isNull();
    }
}
