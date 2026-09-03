package Diginamic.Hello.Controleurs;

import Diginamic.Hello.Departement.Departement;
import Diginamic.Hello.Dto.VilleDto;
import Diginamic.Hello.Export.PdfExporter;
import Diginamic.Hello.Exceptions.VilleException;
import Diginamic.Hello.Service.DepartementService;
import Diginamic.Hello.Service.VilleService;
import Diginamic.Hello.Utils.ValidationUtils;
import com.itextpdf.text.DocumentException;
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
 * Controleur REST de gestion des departements et des recherches de villes par departement.
 * Ne fait pas d'acces aux données ni controle métier : délègue tout aux services.
 */
@RestController
@RequestMapping("/departements")
public class DepartementControleur {

    private final DepartementService departementService;
    private final VilleService villeService;

    public DepartementControleur(DepartementService departementService, VilleService villeService) {
        this.departementService = departementService;
        this.villeService = villeService;
    }

    // -------------------------
    // GET tous les départements
    // -------------------------
    @Operation(summary = "Retourne la liste de tous les départements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des départements",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Departement.class))))
    })
    @GetMapping
    public List<Departement> getDepartements() {
        return departementService.extractDepartements();
    }

    // -------------------------
    // GET departement par id
    // -------------------------
    @Operation(summary = "Retourne un département à partir de son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Département trouvé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Departement.class))),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public Departement getDepartementById(
            @Parameter(description = "Identifiant du département", example = "1", required = true)
            @PathVariable int id) throws VilleException {
        return departementService.extractDepartement(id);
    }

    // -------------------------
    // POST ajouter departement
    // -------------------------
    @Operation(summary = "Ajoute un nouveau département")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Département ajouté avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Departement.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Erreur de validation",
                    content = @Content)
    })
    @PostMapping
    public List<Departement> ajouterDepartement(
            @Parameter(description = "Département à ajouter", required = true)
            @Valid @RequestBody Departement departement,
            BindingResult bindingResult) throws VilleException {

        if (bindingResult.hasErrors()) {
            throw new VilleException(ValidationUtils.extraireMessages(bindingResult));
        }

        return departementService.insertDepartement(departement);
    }

    // -------------------------
    // PUT modifier département
    // -------------------------
    @Operation(summary = "Modifie un département existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Département modifié avec succès",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Departement.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé ou données invalides")
    })
    @PutMapping("/{id}")
    public List<Departement> modifierDepartement(
            @Parameter(description = "Identifiant du département à modifier", example = "1")
            @PathVariable int id,
            @Valid @RequestBody Departement departement,
            BindingResult bindingResult) throws VilleException {

        if (bindingResult.hasErrors()) {
            throw new VilleException(ValidationUtils.extraireMessages(bindingResult));
        }

        return departementService.modifierDepartement(id, departement);
    }

    // -------------------------
    // DELETE département
    // -------------------------
    @Operation(summary = "Supprime un département")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Département supprimé",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Departement.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé")
    })
    @DeleteMapping("/{id}")
    public List<Departement> supprimerDepartement(
            @Parameter(description = "Identifiant du département à supprimer", example = "1")
            @PathVariable int id) throws VilleException {
        return departementService.supprimerDepartement(id);
    }

    // -------------------------
    // GET les n villes les plus peuplées d'un département
    // -------------------------
    @Operation(summary = "Retourne les n villes les plus peuplées d'un département")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes trouvées",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé")
    })
    @GetMapping("/{id}/villes/top/{n}")
    public List<VilleDto> getVillesPlusPeuplees(
            @Parameter(description = "Identifiant du département", example = "1")
            @PathVariable int id,
            @Parameter(description = "Nombre de villes à retourner", example = "3")
            @PathVariable int n) throws VilleException {
        return villeService.extractVillesPlusPeuplees(id, n);
    }

    // -------------------------
    // GET les villes d'un département dont la population est comprise entre min et max
    // -------------------------
    @Operation(summary = "Retourne les villes d'un département dont la population est comprise entre min et max")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes trouvées",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé")
    })
    @GetMapping("/{id}/villes/population/{min}/{max}")
    public List<VilleDto> getVillesParPopulation(
            @Parameter(description = "Identifiant du département", example = "1")
            @PathVariable int id,
            @Parameter(description = "Population minimale", example = "10000")
            @PathVariable int min,
            @Parameter(description = "Population maximale", example = "500000")
            @PathVariable int max) throws VilleException {
        return villeService.rechercherParDepartementEtPopulationEntre(id, min, max);
    }

    // -------------------------
    // GET les villes d'un département dont la population est supérieure à min
    // -------------------------
    @Operation(summary = "Retourne les villes d'un département dont la population est supérieure à min")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Liste des villes trouvées",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé")
    })
    @GetMapping("/{id}/villes/population/{min}")
    public List<VilleDto> getVillesParPopulationMin(
            @Parameter(description = "Identifiant du département", example = "1")
            @PathVariable int id,
            @Parameter(description = "Population minimale", example = "10000")
            @PathVariable int min) throws VilleException {
        return villeService.rechercherParDepartementEtPopulationMin(id, min);
    }

    // -------------------------
    // GET export PDF de la fiche d'un département (à partir de son code)
    // -------------------------
    @Operation(summary = "Exporte en PDF la fiche d'un département (code, nom, liste des villes) à partir de son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fichier PDF généré"),
            @ApiResponse(responseCode = "400",
                    description = "Département non trouvé")
    })
    @GetMapping("/export/pdf/{code}")
    public ResponseEntity<byte[]> exporterDepartementPdf(
            @Parameter(description = "Code du département", example = "31")
            @PathVariable String code) throws VilleException {

        Departement departement = departementService.extractDepartementParCode(code);
        List<VilleDto> villes = villeService.extractVillesParDepartement(departement.getId());

        byte[] pdf;
        try {
            pdf = PdfExporter.genererFichePdfDepartement(departement, villes);
        } catch (DocumentException e) {
            throw new VilleException("Erreur lors de la génération du PDF : " + e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("departement-" + code + ".pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
