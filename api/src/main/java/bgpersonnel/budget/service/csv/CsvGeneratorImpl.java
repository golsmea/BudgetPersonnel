package bgpersonnel.budget.service.csv;

import bgpersonnel.budget.exeception.GenerationRapportException;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;

@Service
@Slf4j
public class CsvGeneratorImpl<T> implements CsvGenerator<T> {
    public ByteArrayInputStream generateCSV(List<T> donnees, String[] header) {

        // Créer le contenu du rapport CSV en utilisant OpenCSV
        StringWriter writer = new StringWriter();

        CSVWriter csvWriter = new CSVWriter(writer, ICSVWriter.DEFAULT_SEPARATOR,
                ICSVWriter.NO_QUOTE_CHARACTER, ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                ICSVWriter.DEFAULT_LINE_END);

        csvWriter.writeNext(header);

        // Écrire les données sans inclure le header automatique skip les noms des colonnes
        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
                .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                .withQuotechar(ICSVWriter.NO_QUOTE_CHARACTER)
                .withOrderedResults(false)
                .build();

        try {
            beanToCsv.write(donnees);
            csvWriter.flush();
            return new ByteArrayInputStream(writer.toString().getBytes());
        } catch (Exception e) {
            // Gérer les exceptions
            log.error("Erreur lors de la génération du fichier CSV", e);
            throw new GenerationRapportException("Erreur lors de la génération du fichier CSV: " + e.getMessage());
        }

    }


}
