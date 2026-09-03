package Diginamic.Hello.Repository;

import Diginamic.Hello.Ville.Ville;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Ville.
 * Remplace VilleDao : plus besoin de manipuler l'EntityManager à la main,
 * Spring Data génère les requêtes à partir du nom des méthodes.
 */
public interface VilleRepository extends JpaRepository<Ville, Integer> {

    /**
     * Retourne, de façon paginée, toutes les villes en base.
     */
    Page<Ville> findAll(Pageable pageable);

    /**
     * Extrait la ville dont le nom (exact) est passé en paramètre.
     */
    Optional<Ville> findByNom(String nom);

    /**
     * Extrait la ville dont le nom (exact) est passé en paramètre, au sein d'un département donné.
     * Utilisé pour vérifier qu'un nom de ville n'est pas déjà utilisé dans ce département avant insertion.
     */
    Optional<Ville> findByNomAndDepartementId(String nom, int idDepartement);

    /**
     * Recherche de toutes les villes dont le nom commence par la chaîne donnée.
     */
    List<Ville> findByNomStartingWith(String prefixe);

    /**
     * Recherche de toutes les villes appartenant à un département donné.
     */
    List<Ville> findByDepartementId(int idDepartement);

    /**
     * Recherche de toutes les villes dont la population est supérieure à min,
     * retournées par population décroissante.
     */
    List<Ville> findByPopulationGreaterThanOrderByPopulationDesc(int min);

    /**
     * Recherche de toutes les villes dont la population est supérieure à min
     * et inférieure à max, retournées par population décroissante.
     */
    List<Ville> findByPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(int min, int max);

    /**
     * Recherche de toutes les villes d'un département dont la population est
     * supérieure à min, retournées par population décroissante.
     */
    List<Ville> findByDepartementIdAndPopulationGreaterThanOrderByPopulationDesc(int idDepartement, int min);

    /**
     * Recherche de toutes les villes d'un département dont la population est
     * supérieure à min et inférieure à max, retournées par population décroissante.
     */
    List<Ville> findByDepartementIdAndPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(
            int idDepartement, int min, int max);

    /**
     * Recherche des villes d'un département, triées par population décroissante.
     * Combiné à un Pageable (PageRequest.of(0, n)), permet d'obtenir les n villes
     * les plus peuplées du département (n étant dynamique, contrairement à un findTopNBy...).
     */
    List<Ville> findByDepartementIdOrderByPopulationDesc(int idDepartement, Pageable pageable);
}
