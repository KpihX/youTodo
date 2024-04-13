package com.kpihx_lab.youtodo.domain;

import com.kpihx_lab.youtodo.domain.enumeration.Priorite;
import com.kpihx_lab.youtodo.domain.enumeration.StatutTache;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Tache.
 */
@Table("tache")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 100)
    @Column("titre")
    private String titre;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("date_echeance")
    private LocalDate dateEcheance;

    @NotNull(message = "must not be null")
    @Column("priorite")
    private Priorite priorite;

    @NotNull(message = "must not be null")
    @Column("statut")
    private StatutTache statut;

    @Transient
    private Categorie categorie;

    @Transient
    private User user;

    @Column("categorie_id")
    private Long categorieId;

    @Column("user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tache id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Tache titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public Tache description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateEcheance() {
        return this.dateEcheance;
    }

    public Tache dateEcheance(LocalDate dateEcheance) {
        this.setDateEcheance(dateEcheance);
        return this;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public Priorite getPriorite() {
        return this.priorite;
    }

    public Tache priorite(Priorite priorite) {
        this.setPriorite(priorite);
        return this;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public StatutTache getStatut() {
        return this.statut;
    }

    public Tache statut(StatutTache statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutTache statut) {
        this.statut = statut;
    }

    public Categorie getCategorie() {
        return this.categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
        this.categorieId = categorie != null ? categorie.getId() : null;
    }

    public Tache categorie(Categorie categorie) {
        this.setCategorie(categorie);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Tache user(User user) {
        this.setUser(user);
        return this;
    }

    public Long getCategorieId() {
        return this.categorieId;
    }

    public void setCategorieId(Long categorie) {
        this.categorieId = categorie;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tache)) {
            return false;
        }
        return getId() != null && getId().equals(((Tache) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tache{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateEcheance='" + getDateEcheance() + "'" +
            ", priorite='" + getPriorite() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
