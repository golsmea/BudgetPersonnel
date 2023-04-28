package bgpersonnel.budget.service.csv;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface CsvGenerator<T> {
    /**
     * Cette méthode permet de générer un fichier CSV à partir d'une liste de données et d'un header
     *
     * @param donnees la liste de données
     * @param header  le header du fichier
     */
    ByteArrayInputStream generateCSV(List<T> donnees, String[] header);
}
