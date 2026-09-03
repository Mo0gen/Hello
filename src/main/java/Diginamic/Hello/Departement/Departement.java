package Diginamic.Hello.Departement;

import Diginamic.Hello.Ville.Ville;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Entité JPA représentant un département.
 * Un département peut posséder plusieurs villes.
 */
@Entity
@Table(name = "departement")
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Le code du département est obligatoire")
    private String code;

    // Pas de contrainte @NotNull ici : un département peut être créé automatiquement
    // (avec uniquement son code) lors de l'ajout d'une ville, cf. VilleService.resolveDepartement.
    private String nom;

    /**
     * @JsonIgnore évite les boucles infinies lors de la sérialisation JSON
     * (Ville -> Departement -> List<Ville> -> ...).
     * Solution temporaire, en attendant les DTO du TP 9.
     */
    @OneToMany(mappedBy = "departement")
    @JsonIgnore
    private List<Ville> villes;

    /**
     * Constructeur sans paramètre requis par JPA.
     */
    public Departement() {
    }

    public Departement(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Ville> getVilles() {
        return villes;
    }

    public void setVilles(List<Ville> villes) {
        this.villes = villes;
    }
}
