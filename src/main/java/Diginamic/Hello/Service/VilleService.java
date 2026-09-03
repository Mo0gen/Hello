package Diginamic.Hello.Service;

import Diginamic.Hello.Departement.Departement;
import Diginamic.Hello.Dto.VilleDto;
import Diginamic.Hello.Exceptions.VilleException;
import Diginamic.Hello.Mapper.VilleMapper;
import Diginamic.Hello.Repository.DepartementRepository;
import Diginamic.Hello.Repository.VilleRepository;
import Diginamic.Hello.Ville.Ville;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche service pour l'entité Ville.
 * Réalise les contrôles métier, résout le département associé à partir du VilleDto,
 * et délègue tous les accès aux données à VilleRepository / DepartementRepository.
 */
@Service
public class VilleService {

    private final VilleRepository villeRepository;
    private final DepartementRepository departementRepository;

    public VilleService(VilleRepository villeRepository, DepartementRepository departementRepository) {
        this.villeRepository = villeRepository;
        this.departementRepository = departementRepository;
    }

    /**
     * Extrait et retourne les villes qui sont en base, de façon paginée.
     */
    public List<VilleDto> extractVilles(int page, int taille) {
        Page<Ville> resultat = villeRepository.findAll(PageRequest.of(page, taille));
        return VilleMapper.toDtoList(resultat.getContent());
    }

    /**
     * Extrait la ville dont l'id est passé en paramètre.
     */
    public VilleDto extractVille(int idVille) throws VilleException {
        return VilleMapper.toDto(findVilleEntity(idVille));
    }

    /**
     * Extrait la ville dont le nom est passé en paramètre.
     */
    public VilleDto extractVille(String nom) throws VilleException {
        return VilleMapper.toDto(findVilleEntity(nom));
    }

    /**
     * Recherche de toutes les villes dont le nom commence par le préfixe donné.
     */
    public List<VilleDto> rechercherParPrefixe(String prefixe) {
        return VilleMapper.toDtoList(villeRepository.findByNomStartingWith(prefixe));
    }

    /**
     * Recherche de toutes les villes dont la population est supérieure à min.
     */
    public List<VilleDto> rechercherParPopulationMin(int min) {
        return VilleMapper.toDtoList(villeRepository.findByPopulationGreaterThanOrderByPopulationDesc(min));
    }

    /**
     * Recherche de toutes les villes dont la population est comprise entre min et max.
     */
    public List<VilleDto> rechercherParPopulationEntre(int min, int max) {
        return VilleMapper.toDtoList(
                villeRepository.findByPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(min, max));
    }

    /**
     * Recherche de toutes les villes d'un département dont la population est supérieure à min.
     */
    public List<VilleDto> rechercherParDepartementEtPopulationMin(int idDepartement, int min) throws VilleException {
        verifierDepartementExiste(idDepartement);
        return VilleMapper.toDtoList(
                villeRepository.findByDepartementIdAndPopulationGreaterThanOrderByPopulationDesc(idDepartement, min));
    }

    /**
     * Recherche de toutes les villes d'un département dont la population est comprise entre min et max.
     */
    public List<VilleDto> rechercherParDepartementEtPopulationEntre(int idDepartement, int min, int max)
            throws VilleException {
        verifierDepartementExiste(idDepartement);
        return VilleMapper.toDtoList(
                villeRepository.findByDepartementIdAndPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(
                        idDepartement, min, max));
    }

    /**
     * Retourne toutes les villes appartenant à un département donné.
     */
    public List<VilleDto> extractVillesParDepartement(int idDepartement) throws VilleException {
        verifierDepartementExiste(idDepartement);
        return VilleMapper.toDtoList(villeRepository.findByDepartementId(idDepartement));
    }

    /**
     * Retourne les n villes les plus peuplées du département passé en paramètre.
     */
    public List<VilleDto> extractVillesPlusPeuplees(int idDepartement, int n) throws VilleException {
        verifierDepartementExiste(idDepartement);
        PageRequest pageable = PageRequest.of(0, n);
        return VilleMapper.toDtoList(villeRepository.findByDepartementIdOrderByPopulationDesc(idDepartement, pageable));
    }

    /**
     * Insère une nouvelle ville en base et retourne la liste des villes après insertion.
     * Le département associé est résolu à partir du code/id porté par le DTO
     * (et créé à la volée si le code est renseigné mais inconnu).
     * Deux villes ne peuvent pas porter le même nom au sein d'un même département
     * (le même nom reste en revanche autorisé dans deux départements différents).
     */
    @Transactional
    public List<VilleDto> insertVille(VilleDto villeDto) throws VilleException {
        Departement departement = resolveDepartement(villeDto);

        if (villeRepository.findByNomAndDepartementId(villeDto.getNom(), departement.getId()).isPresent()) {
            throw new VilleException(
                    "Une ville avec le nom '" + villeDto.getNom() + "' existe déjà dans ce département");
        }

        Ville ville = VilleMapper.toEntity(villeDto);
        ville.setDepartement(departement);
        villeRepository.save(ville);

        return VilleMapper.toDtoList(villeRepository.findAll());
    }

    /**
     * Modifie la ville dont l'identifiant est passé en paramètre. Les nouvelles données,
     * y compris le département, sont portées par le DTO villeModifiee.
     * Retourne la liste des villes après modification.
     */
    @Transactional
    public List<VilleDto> modifierVille(int idVille, VilleDto villeModifiee) throws VilleException {
        Ville ville = findVilleEntity(idVille);
        ville.setNom(villeModifiee.getNom());
        ville.setPopulation(villeModifiee.getPopulation());
        ville.setDepartement(resolveDepartement(villeModifiee));
        villeRepository.save(ville);

        return VilleMapper.toDtoList(villeRepository.findAll());
    }

    /**
     * Supprime la ville dont l'id est passé en paramètre et retourne la liste des villes après suppression.
     */
    @Transactional
    public List<VilleDto> supprimerVille(int idVille) throws VilleException {
        Ville ville = findVilleEntity(idVille);
        villeRepository.delete(ville);

        return VilleMapper.toDtoList(villeRepository.findAll());
    }

    private Ville findVilleEntity(int idVille) throws VilleException {
        return villeRepository.findById(idVille)
                .orElseThrow(() -> new VilleException("Aucune ville avec l'id " + idVille + " n'a été trouvée"));
    }

    private Ville findVilleEntity(String nom) throws VilleException {
        return villeRepository.findByNom(nom)
                .orElseThrow(() -> new VilleException("Aucune ville avec le nom '" + nom + "' n'a été trouvée"));
    }

    private void verifierDepartementExiste(int idDepartement) throws VilleException {
        if (departementRepository.findById(idDepartement).isEmpty()) {
            throw new VilleException("Aucun département avec l'id " + idDepartement + " n'a été trouvé");
        }
    }

    /**
     * Résout le département associé à une ville à partir du VilleDto :
     * - si idDepartement est renseigné et correspond à un département existant, on l'utilise ;
     * - sinon, si codeDepartement est renseigné, on recherche par code, et on crée
     *   le département s'il n'existe pas encore ;
     * - sinon (aucun département trouvé), on jette une exception "département inconnu".
     */
    private Departement resolveDepartement(VilleDto dto) throws VilleException {
        Departement departement = null;

        if (dto.getIdDepartement() != null) {
            departement = departementRepository.findById(dto.getIdDepartement()).orElse(null);
        }

        if (departement == null && dto.getCodeDepartement() != null && !dto.getCodeDepartement().isBlank()) {
            departement = departementRepository.findByCode(dto.getCodeDepartement()).orElse(null);
            if (departement == null) {
                departement = new Departement();
                departement.setCode(dto.getCodeDepartement());
                departement = departementRepository.save(departement);
            }
        }

        if (departement == null) {
            throw new VilleException("Département inconnu");
        }

        return departement;
    }
}
