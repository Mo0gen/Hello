package Diginamic.Hello.Mapper;

import Diginamic.Hello.Departement.Departement;
import Diginamic.Hello.Dto.VilleDto;
import Diginamic.Hello.Ville.Ville;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convertit les entités Ville en VilleDto et inversement.
 * Le mapping DTO -> entité ne résout pas le département associé :
 * cette résolution nécessite un accès aux données et reste de la responsabilité du service.
 */
public final class VilleMapper {

    private VilleMapper() {
    }

    public static VilleDto toDto(Ville ville) {
        VilleDto dto = new VilleDto();
        dto.setId(ville.getId());
        dto.setNom(ville.getNom());
        dto.setPopulation(ville.getPopulation());

        Departement departement = ville.getDepartement();
        if (departement != null) {
            dto.setIdDepartement(departement.getId());
            dto.setCodeDepartement(departement.getCode());
            dto.setNomDepartement(departement.getNom());
        }

        return dto;
    }

    public static List<VilleDto> toDtoList(List<Ville> villes) {
        return villes.stream()
                .map(VilleMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Ville toEntity(VilleDto dto) {
        Ville ville = new Ville();
        ville.setId(dto.getId());
        ville.setNom(dto.getNom());
        ville.setPopulation(dto.getPopulation());
        return ville;
    }
}
