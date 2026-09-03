package Diginamic.Hello.Service;

import Diginamic.Hello.Departement.Departement;
import Diginamic.Hello.Dto.VilleDto;
import Diginamic.Hello.Exceptions.VilleException;
import Diginamic.Hello.Repository.DepartementRepository;
import Diginamic.Hello.Repository.VilleRepository;
import Diginamic.Hello.Ville.Ville;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de VilleService avec un vrai contexte Spring et une base H2 en mémoire
 * (voir src/test/resources/application.properties) : pas de mock ici, on vérifie
 * le comportement réel du service au-dessus d'une vraie base de données.
 * <p>
 * Chaque test est englobé dans une transaction annulée à la fin (@Transactional),
 * donc les données injectées dans setUp() ne fuient jamais d'un test à l'autre.
 */
@SpringBootTest
@Transactional
class VilleServiceTest {

    @Autowired
    private VilleService villeService;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private DepartementRepository departementRepository;

    private Departement herault;
    private Departement gard;

    @BeforeEach
    void setUp() {
        villeRepository.deleteAll();
        departementRepository.deleteAll();

        herault = departementRepository.save(new Departement("34", "Hérault"));
        gard = departementRepository.save(new Departement("30", "Gard"));

        villeRepository.save(creerVille("Montpellier", 300_000, herault));
        villeRepository.save(creerVille("Sète", 45_000, herault));
        villeRepository.save(creerVille("Béziers", 78_000, herault));
        villeRepository.save(creerVille("Nîmes", 150_000, gard));
    }

    private Ville creerVille(String nom, int population, Departement departement) {
        Ville ville = new Ville();
        ville.setNom(nom);
        ville.setPopulation(population);
        ville.setDepartement(departement);
        return ville;
    }

    // -------------------------
    // extractVilles (paginé)
    // -------------------------

    @Test
    void extractVilles_doitRetournerLaTailleDePageDemandee() {
        List<VilleDto> page = villeService.extractVilles(0, 2);

        assertThat(page).hasSize(2);
    }

    // -------------------------
    // extractVille (par id / par nom)
    // -------------------------

    @Test
    void extractVille_parId_doitRetournerLaVille() throws VilleException {
        Ville montpellier = villeRepository.findByNom("Montpellier").orElseThrow();

        VilleDto dto = villeService.extractVille(montpellier.getId());

        assertThat(dto.getNom()).isEqualTo("Montpellier");
        assertThat(dto.getCodeDepartement()).isEqualTo("34");
    }

    @Test
    void extractVille_parId_inconnu_doitLeverUneException() {
        assertThatThrownBy(() -> villeService.extractVille(999_999))
                .isInstanceOf(VilleException.class);
    }

    @Test
    void extractVille_parNom_doitRetournerLaVille() throws VilleException {
        VilleDto dto = villeService.extractVille("Nîmes");

        assertThat(dto.getPopulation()).isEqualTo(150_000);
    }

    @Test
    void extractVille_parNom_inconnu_doitLeverUneException() {
        assertThatThrownBy(() -> villeService.extractVille("Ville-Imaginaire"))
                .isInstanceOf(VilleException.class);
    }

    // -------------------------
    // Recherches
    // -------------------------

    @Test
    void rechercherParPrefixe_doitRetournerLesVillesCorrespondantes() {
        List<VilleDto> resultat = villeService.rechercherParPrefixe("Sè");

        assertThat(resultat).extracting(VilleDto::getNom).containsExactly("Sète");
    }

    @Test
    void rechercherParPopulationMin_doitRetournerLesVillesTrieesParPopulationDecroissante() {
        List<VilleDto> resultat = villeService.rechercherParPopulationMin(50_000);

        assertThat(resultat).extracting(VilleDto::getNom)
                .containsExactly("Montpellier", "Nîmes", "Béziers");
    }

    @Test
    void rechercherParPopulationEntre_doitRetournerLesVillesDansLIntervalle() {
        List<VilleDto> resultat = villeService.rechercherParPopulationEntre(50_000, 200_000);

        assertThat(resultat).extracting(VilleDto::getNom).containsExactly("Nîmes", "Béziers");
    }

    @Test
    void rechercherParDepartementEtPopulationMin_doitFiltrerParDepartement() throws VilleException {
        List<VilleDto> resultat = villeService.rechercherParDepartementEtPopulationMin(herault.getId(), 40_000);

        assertThat(resultat).extracting(VilleDto::getNom)
                .containsExactly("Montpellier", "Béziers", "Sète");
    }

    @Test
    void rechercherParDepartementEtPopulationEntre_doitFiltrerParDepartementEtIntervalle() throws VilleException {
        List<VilleDto> resultat =
                villeService.rechercherParDepartementEtPopulationEntre(herault.getId(), 40_000, 100_000);

        assertThat(resultat).extracting(VilleDto::getNom).containsExactly("Béziers", "Sète");
    }

    @Test
    void rechercherParDepartementEtPopulationMin_departementInconnu_doitLeverUneException() {
        assertThatThrownBy(() -> villeService.rechercherParDepartementEtPopulationMin(999_999, 0))
                .isInstanceOf(VilleException.class);
    }

    @Test
    void extractVillesPlusPeuplees_doitRetournerLesNVillesLesPlusPeupleesDuDepartement() throws VilleException {
        List<VilleDto> resultat = villeService.extractVillesPlusPeuplees(herault.getId(), 2);

        assertThat(resultat).extracting(VilleDto::getNom).containsExactly("Montpellier", "Béziers");
    }

    @Test
    void extractVillesParDepartement_doitRetournerToutesLesVillesDuDepartement() throws VilleException {
        List<VilleDto> resultat = villeService.extractVillesParDepartement(gard.getId());

        assertThat(resultat).extracting(VilleDto::getNom).containsExactly("Nîmes");
    }

    // -------------------------
    // insertVille
    // -------------------------

    @Test
    void insertVille_doitAjouterLaVilleEtLaRetrouverEnBase() throws VilleException {
        VilleDto nouvelle = new VilleDto();
        nouvelle.setNom("Lunel");
        nouvelle.setPopulation(28_000);
        nouvelle.setIdDepartement(herault.getId());

        List<VilleDto> toutesLesVilles = villeService.insertVille(nouvelle);

        assertThat(toutesLesVilles).extracting(VilleDto::getNom).contains("Lunel");
        assertThat(villeRepository.findByNomAndDepartementId("Lunel", herault.getId())).isPresent();
    }

    @Test
    void insertVille_nomDejaUtiliseDansLeMemeDepartement_doitLeverUneException() {
        VilleDto doublon = new VilleDto();
        doublon.setNom("Montpellier");
        doublon.setPopulation(1_000);
        doublon.setIdDepartement(herault.getId());

        assertThatThrownBy(() -> villeService.insertVille(doublon))
                .isInstanceOf(VilleException.class)
                .hasMessageContaining("existe déjà");
    }

    @Test
    void insertVille_memeNomDansUnAutreDepartement_doitEtreAutorise() throws VilleException {
        VilleDto memeNomAutreDepartement = new VilleDto();
        memeNomAutreDepartement.setNom("Montpellier");
        memeNomAutreDepartement.setPopulation(500);
        memeNomAutreDepartement.setIdDepartement(gard.getId());

        List<VilleDto> resultat = villeService.insertVille(memeNomAutreDepartement);

        assertThat(resultat).filteredOn(v -> v.getNom().equals("Montpellier")).hasSize(2);
    }

    @Test
    void insertVille_departementInconnu_doitLeverUneException() {
        VilleDto sansDepartementValide = new VilleDto();
        sansDepartementValide.setNom("Nulle-Part");
        sansDepartementValide.setPopulation(100);
        sansDepartementValide.setIdDepartement(999_999);

        assertThatThrownBy(() -> villeService.insertVille(sansDepartementValide))
                .isInstanceOf(VilleException.class);
    }

    // -------------------------
    // modifierVille
    // -------------------------

    @Test
    void modifierVille_doitMettreAJourLaVille() throws VilleException {
        Ville sete = villeRepository.findByNom("Sète").orElseThrow();

        VilleDto modif = new VilleDto();
        modif.setNom("Sète");
        modif.setPopulation(46_000);
        modif.setIdDepartement(gard.getId());

        villeService.modifierVille(sete.getId(), modif);
        VilleDto miseAJour = villeService.extractVille(sete.getId());

        assertThat(miseAJour.getPopulation()).isEqualTo(46_000);
        assertThat(miseAJour.getCodeDepartement()).isEqualTo("30");
    }

    @Test
    void modifierVille_idInconnu_doitLeverUneException() {
        VilleDto modif = new VilleDto();
        modif.setNom("Peu importe");
        modif.setPopulation(10);
        modif.setIdDepartement(herault.getId());

        assertThatThrownBy(() -> villeService.modifierVille(999_999, modif))
                .isInstanceOf(VilleException.class);
    }

    // -------------------------
    // supprimerVille
    // -------------------------

    @Test
    void supprimerVille_doitRetirerLaVilleDeLaBase() throws VilleException {
        Ville beziers = villeRepository.findByNom("Béziers").orElseThrow();

        villeService.supprimerVille(beziers.getId());

        assertThat(villeRepository.findById(beziers.getId())).isEmpty();
    }

    @Test
    void supprimerVille_idInconnu_doitLeverUneException() {
        assertThatThrownBy(() -> villeService.supprimerVille(999_999))
                .isInstanceOf(VilleException.class);
    }
}
