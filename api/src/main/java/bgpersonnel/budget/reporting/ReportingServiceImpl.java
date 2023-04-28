package bgpersonnel.budget.reporting;


import bgpersonnel.budget.authentification.common.services.UserService;
import bgpersonnel.budget.exeception.TypeRapportNotFoundException;
import bgpersonnel.budget.service.csv.CsvGenerator;
import bgpersonnel.budget.service.excel.ExcelGenerator;
import bgpersonnel.budget.service.pdf.PdfGenerator;
import bgpersonnel.budget.transaction.TransactionReportDto;
import bgpersonnel.budget.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
class ReportingServiceImpl implements ReportingService {

    private final CsvGenerator<TransactionReportDto> csvGenerator;
    private final ExcelGenerator<TransactionReportDto> excelGenerator;
    private final PdfGenerator<TransactionReportDto> pdfGenerator;
    private final TransactionRepository transactionRepository;

    public ReportingServiceImpl(CsvGenerator<TransactionReportDto> csvGenerator,
                                ExcelGenerator<TransactionReportDto> excelGenerator,
                                PdfGenerator<TransactionReportDto> pdfGenerator,
                                TransactionRepository transactionRepository) {
        this.csvGenerator = csvGenerator;
        this.excelGenerator = excelGenerator;
        this.pdfGenerator = pdfGenerator;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ByteArrayInputStream generateReport(ReportRequest reportRequest) throws TypeRapportNotFoundException {

        Long userId = UserService.getIdConnectedUser();
        List<TransactionReportDto> transactions = transactionRepository.getTransactionsByUserBetweenDatesAndCategory(userId, reportRequest.getStartDate(), reportRequest.getEndDate(), reportRequest.getCategoryId());
        String[] header = {"Nom", "Montant", "Date", "Description", "Type de transaction"};
        return switch (reportRequest.getReportType()) {
            case CSV -> csvGenerator.generateCSV(transactions, header);
            case XLS -> excelGenerator.generateExcel(transactions, header);
            case PDF -> pdfGenerator.generatePdf(transactions, "/report.html", header);
            default -> throw new TypeRapportNotFoundException("Le type de rapport demandé n'existe pas" + reportRequest.getReportType());
        };

    }


}
