package Diginamic.Hello.Export;

import Diginamic.Hello.Departement.Departement;
import Diginamic.Hello.Dto.VilleDto;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Génère la fiche PDF d'un département : titre (nom du département), code, nom,
 * et la liste des villes de ce département (nom + population).
 */
public final class PdfExporter {

    private PdfExporter() {
    }

    public static byte[] genererFichePdfDepartement(Departement departement, List<VilleDto> villes)
            throws DocumentException {

        String titre = (departement.getNom() != null && !departement.getNom().isBlank())
                ? departement.getNom()
                : "Département " + departement.getCode();

        Font policeTitre = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font policeLabel = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font policeTexte = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font policeEntete = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        Paragraph paragrapheTitre = new Paragraph(titre, policeTitre);
        paragrapheTitre.setAlignment(Element.ALIGN_CENTER);
        paragrapheTitre.setSpacingAfter(20);
        document.add(paragrapheTitre);

        document.add(new Paragraph("Code du département : " + departement.getCode(), policeTexte));
        document.add(new Paragraph(
                "Nom du département : " + (departement.getNom() != null ? departement.getNom() : "-"),
                policeTexte));

        Paragraph sousTitre = new Paragraph("Liste des villes", policeLabel);
        sousTitre.setSpacingBefore(20);
        sousTitre.setSpacingAfter(8);
        document.add(sousTitre);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 1});

        table.addCell(celluleEntete("Nom", policeEntete));
        table.addCell(celluleEntete("Population", policeEntete));

        if (villes.isEmpty()) {
            PdfPCell celluleVide = new PdfPCell(new Phrase("Aucune ville trouvée", policeTexte));
            celluleVide.setColspan(2);
            table.addCell(celluleVide);
        } else {
            for (VilleDto ville : villes) {
                table.addCell(new Phrase(ville.getNom(), policeTexte));
                table.addCell(new Phrase(String.valueOf(ville.getPopulation()), policeTexte));
            }
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private static PdfPCell celluleEntete(String texte, Font police) {
        PdfPCell cellule = new PdfPCell(new Phrase(texte, police));
        cellule.setBackgroundColor(BaseColor.DARK_GRAY);
        cellule.setPadding(5);
        return cellule;
    }
}
