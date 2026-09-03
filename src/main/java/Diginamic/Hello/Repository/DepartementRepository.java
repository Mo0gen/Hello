package Diginamic.Hello.Repository;

import Diginamic.Hello.Departement.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Departement.
 * Remplace DepartementDao.
 */
public interface DepartementRepository extends JpaRepository<Departement, Integer> {

    /**
     * Extrait le département dont le code est passé en paramètre.
     */
    Optional<Departement> findByCode(String code);
}
