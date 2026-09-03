package Diginamic.Hello.Ville;

import Diginamic.Hello.Departement.Departement;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entité JPA représentant une ville.
 * Une ville appartient au plus à un seul département.
 */
@Entity
@Table(name = "ville")
public class Ville {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Le nom de la ville est obligatoire")
    @Size(min = 2, message = "Le nom de la ville doit contenir au moins 2 caractères")
    private String nom;

    @Min(value = 1, message = "Le nombre d'habitants doit être supérieur ou égal à 1")
    @Column(name = "nb_habs")
    private int population;

    @ManyToOne
    @JoinColumn(name = "id_dept")
    private Departement departement;

    /**
     * Constructeur sans paramètre requis par JPA.
     */
    public Ville() {
    }

    public Ville(String nom, int population) {
        this.nom = nom;
        this.population = population;
    }

    public Ville(int id, String nom, int population) {
        this.id = id;
        this.nom = nom;
        this.population = population;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
}
