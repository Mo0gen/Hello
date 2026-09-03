package Diginamic.Hello.Service;

import Diginamic.Hello.Departement.Departement;
import Diginamic.Hello.Dto.DepartementApiDto;
import Diginamic.Hello.Exceptions.VilleException;
import Diginamic.Hello.Repository.DepartementRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Couche service pour l'entité Departement.
 * Réalise les contrôles métier et délègue tous les accès aux données à DepartementRepository.
 */
@Service
public class DepartementService {

    private static final String URL_API_DEPARTEMENTS = "https://geo.api.gouv.fr/departements";

    private final DepartementRepository departementRepository;
    private final RestTemplate restTemplate;
    private final boolean applicationInit;

    public DepartementService(DepartementRepository departementRepository,
                               RestTemplate restTemplate,
                               @Value("${application.init}") boolean applicationInit) {
        this.departementRepository = departementRepository;
        this.restTemplate = restTemplate;
        this.applicationInit = applicationInit;
    }

    /**
     * Au démarrage de l'application, met à jour le nom des départements en base à partir
     * de l'API externe {@value #URL_API_DEPARTEMENTS}. Ne fait rien si application.init
     * vaut false dans application.properties (ex: données déjà initialisées).
     */
    @PostConstruct
    public void initData() {
        if (!applicationInit) {
            return;
        }

        try {
            DepartementApiDto[] departementsApi =
                    restTemplate.getForObject(URL_API_DEPARTEMENTS, DepartementApiDto[].class);

            if (departementsApi != null) {
                for (DepartementApiDto departementApi : departementsApi) {
                    departementRepository.findByCode(departementApi.getCode()).ifPresent(departement -> {
                        departement.setNom(departementApi.getNom());
                        departementRepository.save(departement);
                    });
                }
            }

            desactiverInitialisation();
        } catch (RestClientException e) {
            System.err.println(
                    "Impossible d'initialiser les départements depuis l'API externe : " + e.getMessage());
        }
    }

    /**
     * Repasse application.init à false dans application.properties pour que les données
     * ne soient pas réinitialisées au prochain démarrage de l'application.
     */
    private void desactiverInitialisation() {
        try {
            URL url = getClass().getClassLoader().getResource("application.properties");
            if (url == null) {
                return;
            }

            Path chemin = Paths.get(url.toURI());
            List<String> lignes = Files.readAllLines(chemin, StandardCharsets.UTF_8);
            List<String> nouvellesLignes = lignes.stream()
                    .map(ligne -> ligne.startsWith("application.init=") ? "application.init=false" : ligne)
                    .collect(Collectors.toList());

            Files.write(chemin, nouvellesLignes, StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            System.err.println(
                    "Impossible de mettre à jour application.init dans application.properties : " + e.getMessage());
        }
    }

    /**
     * Extrait et retourne les départements qui sont en base.
     */
    public List<Departement> extractDepartements() {
        return departementRepository.findAll();
    }

    /**
     * Extrait le département dont l'id est passé en paramètre.
     */
    public Departement extractDepartement(int idDepartement) throws VilleException {
        return departementRepository.findById(idDepartement)
                .orElseThrow(() -> new VilleException("Aucun département avec l'id " + idDepartement + " n'a été trouvé"));
    }

    /**
     * Extrait le département dont le code est passé en paramètre.
     */
    public Departement extractDepartementParCode(String code) throws VilleException {
        return departementRepository.findByCode(code)
                .orElseThrow(() -> new VilleException("Aucun département avec le code '" + code + "' n'a été trouvé"));
    }

    /**
     * Insère un nouveau département en base et retourne la liste des départements après insertion.
     */
    @Transactional
    public List<Departement> insertDepartement(Departement departement) throws VilleException {
        if (departementRepository.findByCode(departement.getCode()).isPresent()) {
            throw new VilleException("Un département avec le code '" + departement.getCode() + "' existe déjà");
        }
        departementRepository.save(departement);
        return departementRepository.findAll();
    }

    /**
     * Modifie le département dont l'identifiant est passé en paramètre et retourne
     * la liste des départements après modification.
     */
    @Transactional
    public List<Departement> modifierDepartement(int idDepartement, Departement departementModifie) throws VilleException {
        Departement departement = extractDepartement(idDepartement);
        departement.setCode(departementModifie.getCode());
        departement.setNom(departementModifie.getNom());
        departementRepository.save(departement);
        return departementRepository.findAll();
    }

    /**
     * Supprime le département dont l'id est passé en paramètre et retourne
     * la liste des départements après suppression.
     */
    @Transactional
    public List<Departement> supprimerDepartement(int idDepartement) throws VilleException {
        Departement departement = extractDepartement(idDepartement);
        departementRepository.delete(departement);
        return departementRepository.findAll();
    }
}
