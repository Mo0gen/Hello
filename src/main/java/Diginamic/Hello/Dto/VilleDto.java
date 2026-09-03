package Diginamic.Hello.Dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO utilisé pour échanger des données de ville avec l'extérieur (front, Postman...).
 * Contrairement à l'entité Ville, elle porte le code ou l'identifiant du département
 * associé plutôt que l'objet Departement complet.
 */
public class VilleDto {

    private int id;

    @NotNull(message = "Le nom de la ville est obligatoire")
    @Size(min = 2, message = "Le nom de la ville doit contenir au moins 2 caractères")
    private String nom;

    @Min(value = 1, message = "Le nombre d'habitants doit être supérieur ou égal à 1")
    private int population;

    /**
     * Code du département associé (ex: "31"). Optionnel si idDepartement est renseigné.
     */
    private String codeDepartement;

    /**
     * Identifiant du département associé. Optionnel si codeDepartement est renseigné.
     */
    private Integer idDepartement;

    /**
     * Nom du département associé. Renseigné uniquement en sortie (lecture seule) :
     * ignoré à la création/modification d'une ville, calculé par le mapper.
     */
    private String nomDepartement;

    public VilleDto() {
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

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public Integer getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
    }

    public String getNomDepartement() {
        return nomDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }

    /**
     * Contrôle transverse : le code département ou l'identifiant département
     * doit être renseigné pour pouvoir rattacher la ville à un département.
     */
    @AssertTrue(message = "Le code département ou l'identifiant département doit être renseigné")
    public boolean isDepartementRenseigne() {
        return (codeDepartement != null && !codeDepartement.isBlank()) || idDepartement != null;
    }
}
