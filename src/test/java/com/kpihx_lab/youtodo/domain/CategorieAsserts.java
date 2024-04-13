package com.kpihx_lab.youtodo.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class CategorieAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCategorieAllPropertiesEquals(Categorie expected, Categorie actual) {
        assertCategorieAutoGeneratedPropertiesEquals(expected, actual);
        assertCategorieAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCategorieAllUpdatablePropertiesEquals(Categorie expected, Categorie actual) {
        assertCategorieUpdatableFieldsEquals(expected, actual);
        assertCategorieUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCategorieAutoGeneratedPropertiesEquals(Categorie expected, Categorie actual) {
        assertThat(expected)
            .as("Verify Categorie auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCategorieUpdatableFieldsEquals(Categorie expected, Categorie actual) {
        assertThat(expected)
            .as("Verify Categorie relevant properties")
            .satisfies(e -> assertThat(e.getNom()).as("check nom").isEqualTo(actual.getNom()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertCategorieUpdatableRelationshipsEquals(Categorie expected, Categorie actual) {}
}
