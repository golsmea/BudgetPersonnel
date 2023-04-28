package bgpersonnel.budget.service.pdf;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface PdfGenerator<T> {
    /**
     * Cette méthode permet de générer un fichier PDF à partir d'une liste de données et d'un header
     *
     * @param data         la liste de données
     * @param templateHtml le template html du fichier
     * @param header       le header du fichier
     */
    ByteArrayInputStream generatePdf(List<T> data, String templateHtml, String[] header);
}
