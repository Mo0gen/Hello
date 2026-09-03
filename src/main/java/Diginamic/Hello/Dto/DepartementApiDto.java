package Diginamic.Hello.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Représente un département tel que retourné par l'API externe
 * https://geo.api.gouv.fr/departements (seuls code et nom nous intéressent,
 * les autres champs de la réponse - codeRegion, region... - sont ignorés).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartementApiDto {

    private String code;
    private String nom;

    public DepartementApiDto() {
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
}
