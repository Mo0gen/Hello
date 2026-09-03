package Diginamic.Hello.Controleurs;

import Diginamic.Hello.Dto.VilleDto;
import Diginamic.Hello.Export.CsvExporter;
import Diginamic.Hello.Exceptions.VilleException;
import Diginamic.Hello.Service.VilleService;
import Diginamic.Hello.Utils.ValidationUtils;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Contrôleur REST de gestion des villes.
 * N'échange que des VilleDto avec l'extérieur, et ne réalise aucun accès aux données
 * ni contrôle métier : délègue tout à VilleService (qui s'appuie sur VilleRepository).
 */
@RestController
public class VilleControleurs {

    private final VilleService villeService;

    public VilleControleurs(VilleService villeService) {
        this.villeService = villeService;
    }

    // -------------------------
    // GET toutes les villes (paginé)
    // -------------------------
    @Operation(summary = "Retourne la liste des villes, de façon paginée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Page de villes",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class))))
    })
    @GetMapping("/villes")
    public List<VilleDto> getVilles(
            @Parameter(description = "Numéro de la page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre de villes par page", example = "20")
            @RequestParam(defaultValue = "20") int taille) {
        return villeService.extractVilles(page, taille);
    }

    // -------------------------
    // GET ville par id
    // -------------------------
    @Operation(summary = "Retourne une ville à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville trouvée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VilleDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Ville non trouvée",
                    content = @Content)
    })
    @GetMapping("/villes/{id:[0-9]+}")
    public VilleDto getVilleById(
            @Parameter(description = "Identifiant de la ville", example = "1", required = true)
            @PathVariable int id) throws VilleException {
        return villeService.extractVille(id);
    }

    // -------------------------
    // GET ville par nom
    // -------------------------
    @Operation(summary = "Retourne une ville à partir de son nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville trouvée",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VilleDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Ville non trouvée",
                    content = @Content)
    })
    @GetMapping("/villes/nom/{nom}")
    public VilleDto getVilleByNom(
            @Parameter(description = "Nom de la ville", example = "Nice", required = true)
            @PathVariable String nom) throws VilleException {
        return villeService.extractVille(nom);
    }

    // -------------------------
    // GET recherche par préfixe de nom
    // -------------------------
    @Operation(summary = "Recherche les villes dont le nom commence par un préfixe donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes trouvées",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class))))
    })
    @GetMapping("/villes/recherche/nom/{prefixe}")
    public List<VilleDto> rechercherParPrefixe(
            @Parameter(description = "Préfixe du nom recherché", example = "Na")
            @PathVariable String prefixe) {
        return villeService.rechercherParPrefixe(prefixe);
    }

    // -------------------------
    // GET recherche par population minimale
    // -------------------------
    @Operation(summary = "Recherche les villes dont la population est supérieure à min (triées par population décroissante)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes trouvées",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class))))
    })
    @GetMapping("/villes/recherche/population/{min}")
    public List<VilleDto> rechercherParPopulationMin(
            @Parameter(description = "Population minimale", example = "50000")
            @PathVariable int min) {
        return villeService.rechercherParPopulationMin(min);
    }

    // -------------------------
    // GET recherche par population entre min et max
    // -------------------------
    @Operation(summary = "Recherche les villes dont la population est comprise entre min et max (triées par population décroissante)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes trouvées",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class))))
    })
    @GetMapping("/villes/recherche/population/{min}/{max}")
    public List<VilleDto> rechercherParPopulationEntre(
            @Parameter(description = "Population minimale", example = "30000")
            @PathVariable int min,
            @Parameter(description = "Population maximale", example = "100000")
            @PathVariable int max) {
        return villeService.rechercherParPopulationEntre(min, max);
    }

    // -------------------------
    // GET export CSV des villes dont la population est supérieure à min
    // -------------------------
    @Operation(summary = "Exporte au format CSV les villes dont la population est supérieure au minimum donné")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fichier CSV généré")
    })
    @GetMapping("/villes/export/csv")
    public ResponseEntity<byte[]> exporterVillesCsv(
            @Parameter(description = "Population minimale", example = "100000")
            @RequestParam int min) {

        List<VilleDto> villes = villeService.rechercherParPopulationMin(min);
        byte[] csv = CsvExporter.genererCsvVilles(villes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("villes.csv").build());

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    // -------------------------
    // POST ajouter ville
    // -------------------------
    @Operation(summary = "Ajoute une nouvelle ville. Le VilleDto doit porter soit codeDepartement, soit idDepartement.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Ville ajoutée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Erreur de validation ou département inconnu",
                    content = @Content)
    })
    @PostMapping("/villes")
    public List<VilleDto> ajouterVille(
            @Parameter(description = "Ville à ajouter", required = true)
            @Valid @RequestBody VilleDto nouvelleVille,
            BindingResult bindingResult) throws VilleException {

        if (bindingResult.hasErrors()) {
            throw new VilleException(ValidationUtils.extraireMessages(bindingResult));
        }

        return villeService.insertVille(nouvelleVille);
    }

    // -------------------------
    // PUT modifier ville
    // -------------------------
    @Operation(summary = "Modifie une ville existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville modifiée avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Ville non trouvée ou données invalides")
    })
    @PutMapping("/villes/{id}")
    public List<VilleDto> modifierVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "2")
            @PathVariable int id,
            @Valid @RequestBody VilleDto modif,
            BindingResult bindingResult) throws VilleException {

        if (bindingResult.hasErrors()) {
            throw new VilleException(ValidationUtils.extraireMessages(bindingResult));
        }

        return villeService.modifierVille(id, modif);
    }

    // -------------------------
    // DELETE ville
    // -------------------------
    @Operation(summary = "Supprime une ville")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ville supprimée",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Ville non trouvée")
    })
    @DeleteMapping("/villes/{id}")
    public List<VilleDto> supprimerVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "3")
            @PathVariable int id) throws VilleException {
        return villeService.supprimerVille(id);
    }
}
