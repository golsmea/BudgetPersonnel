package bgpersonnel.budget.service.excel;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelGenerator<T> {
    /**
     * Cette méthode permet de générer un fichier Excel à partir d'une liste de données et d'un header
     *
     * @param data    la liste de données
     * @param headers le header du fichier
     */
    ByteArrayInputStream generateExcel(List<T> data, String[] headers);
}
