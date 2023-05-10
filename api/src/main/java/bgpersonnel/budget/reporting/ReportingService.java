package bgpersonnel.budget.reporting;

import bgpersonnel.budget.exeception.TypeRapportNotFoundException;

import java.io.ByteArrayInputStream;

public interface ReportingService {
    /**
     * Cette méthode permet de générer un rapport de liste de transactions en fonction du type de rapport demandé
     */
    ByteArrayInputStream generateReport(ReportRequest reportRequest) throws TypeRapportNotFoundException;
}
