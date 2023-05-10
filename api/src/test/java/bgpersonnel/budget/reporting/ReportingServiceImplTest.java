package bgpersonnel.budget.reporting;

import bgpersonnel.budget.authentification.common.services.UserService;
import bgpersonnel.budget.service.csv.CsvGenerator;
import bgpersonnel.budget.service.excel.ExcelGenerator;
import bgpersonnel.budget.service.pdf.PdfGenerator;
import bgpersonnel.budget.transaction.TransactionReportDto;
import bgpersonnel.budget.transaction.TransactionRepository;
import bgpersonnel.budget.transaction.TypeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceImplTest {

    public static final String[] HEADERS = {"Nom", "Montant", "Date", "Description", "Type de transaction"};
    @Mock
    private CsvGenerator csvGenerator;
    @Mock
    private ExcelGenerator excelGenerator;
    @Mock
    private PdfGenerator pdfGenerator;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private ReportingServiceImpl reportingService;

    private List<TransactionReportDto> transactions;
    private ReportRequest reportRequest;


    @BeforeEach
    void setUp() {
        // Creating a ReportRequest object
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);
        long categoryId = 1L;
        reportRequest = new ReportRequest(startDate, endDate, ETypeReport.CSV, categoryId);

        // Mocking the transactionRepository to return some transactions
        transactions = List.of(
                new TransactionReportDto("Transaction 1", LocalDate.of(2022, 2, 15), 100, "Description 1", TypeTransaction.DEPENSE),
                new TransactionReportDto("Transaction 2", LocalDate.of(2022, 3, 20), 200, "Description 2", TypeTransaction.REVENU)
        );
    }

    // test generateCsvReport
    @Test
    void generateCsvReportTest() {
        this.reportRequest.setReportType(ETypeReport.CSV);
        //given
        ByteArrayInputStream csvInputStream = new ByteArrayInputStream("test".getBytes());
        //when
        when(transactionRepository.getTransactionsByUserBetweenDatesAndCategory(anyLong(), any(), any(), anyLong()))
                .thenReturn(transactions);
        when(csvGenerator.generateCSV(anyList(), any())).thenReturn(csvInputStream);


        try (MockedStatic<UserService> utilities = Mockito.mockStatic(UserService.class)) {
            utilities.when(UserService::getIdConnectedUser).thenReturn(1L);
            ByteArrayInputStream reportInputStream = reportingService.generateReport(reportRequest);

        }

        //then
        verify(csvGenerator, times(1)).generateCSV(transactions, HEADERS);

        verify(excelGenerator, never()).generateExcel(anyList(), any());
        verify(pdfGenerator, never()).generatePdf(anyList(), any(), any());
    }

    @Test
    void generateXlsReportTest() {

        //given
        this.reportRequest.setReportType(ETypeReport.XLS);
        ByteArrayInputStream xlsInputStream = new ByteArrayInputStream("test".getBytes());
        //when
        when(transactionRepository.getTransactionsByUserBetweenDatesAndCategory(anyLong(), any(), any(), anyLong()))
                .thenReturn(transactions);
        when(excelGenerator.generateExcel(anyList(), any())).thenReturn(xlsInputStream);

        try (MockedStatic<UserService> utilities = Mockito.mockStatic(UserService.class)) {
            utilities.when(UserService::getIdConnectedUser).thenReturn(1L);
            ByteArrayInputStream reportInputStream = reportingService.generateReport(reportRequest);

        }

        //then
        verify(excelGenerator, times(1)).generateExcel(transactions, HEADERS);

        verify(csvGenerator, never()).generateCSV(anyList(), any());
        verify(pdfGenerator, never()).generatePdf(anyList(), any(), any());

    }

    @Test
    void generatePdfReportTest() {
        //given
        this.reportRequest.setReportType(ETypeReport.PDF);
        ByteArrayInputStream xlsInputStream = new ByteArrayInputStream("test".getBytes());
        //when
        when(transactionRepository.getTransactionsByUserBetweenDatesAndCategory(anyLong(), any(), any(), anyLong()))
                .thenReturn(transactions);
        when(pdfGenerator.generatePdf(anyList(), any(), any())).thenReturn(xlsInputStream);

        try (MockedStatic<UserService> utilities = Mockito.mockStatic(UserService.class)) {
            utilities.when(UserService::getIdConnectedUser).thenReturn(1L);
            ByteArrayInputStream reportInputStream = reportingService.generateReport(reportRequest);

        }

        //then
        verify(pdfGenerator, times(1)).generatePdf(transactions, "/report.html", HEADERS);

        verify(csvGenerator, never()).generateCSV(anyList(), any());
        verify(excelGenerator, never()).generateExcel(anyList(), any());
    }


}
