package Diginamic.Hello.Export;

import Diginamic.Hello.Dto.VilleDto;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Génère un export CSV à partir d'une liste de VilleDto.
 * Structure du fichier : nom, population, code département, nom département.
 */
public final class CsvExporter {

    private static final byte[] BOM_UTF8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private CsvExporter() {
    }

    public static byte[] genererCsvVilles(List<VilleDto> villes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // BOM UTF-8 : permet à Excel d'afficher correctement les caractères accentués.
        out.write(BOM_UTF8, 0, BOM_UTF8.length);

        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("nom,population,codeDepartement,nomDepartement");

            for (VilleDto ville : villes) {
                writer.println(String.join(",",
                        echapper(ville.getNom()),
                        String.valueOf(ville.getPopulation()),
                        echapper(ville.getCodeDepartement()),
                        echapper(ville.getNomDepartement())));
            }
        }

        return out.toByteArray();
    }

    private static String echapper(String valeur) {
        if (valeur == null) {
            return "";
        }
        return "\"" + valeur.replace("\"", "\"\"") + "\"";
    }
}
