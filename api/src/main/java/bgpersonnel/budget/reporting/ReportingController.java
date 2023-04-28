package bgpersonnel.budget.reporting;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
public class ReportingController {

    private final ReportingService reportingService;

    private static final Map<ETypeReport, String> FILENAMES_BY_TYPE = Map.of(
            ETypeReport.CSV, "report.csv",
            ETypeReport.XLS, "report.xlsx",
            ETypeReport.PDF, "report.pdf"
    );

    private static final Map<ETypeReport, MediaType> CONTENT_TYPES_BY_TYPE = Map.of(
            ETypeReport.CSV, MediaType.parseMediaType("text/csv"),
            ETypeReport.XLS, MediaType.parseMediaType("application/vnd.ms-excel"),
            ETypeReport.PDF, MediaType.parseMediaType("application/pdf")
    );

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    /**
     * Cette méthode permet de générer un rapport de liste de transactions en fonction du type de rapport demandé
     */
    @OpenAPI30
    @Operation(summary = "Generate report", description = "Generate report", tags = {"reporting"})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Report request",
            required = true,
            content = @Content(schema = @Schema(implementation = ReportRequest.class))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping
    public ResponseEntity<Resource> generateReport(@RequestBody ReportRequest reportRequest) {
        String filename = FILENAMES_BY_TYPE.get(reportRequest.getReportType());
        MediaType contentType = CONTENT_TYPES_BY_TYPE.get(reportRequest.getReportType());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(contentType)
                .body(new InputStreamResource(reportingService.generateReport(reportRequest)));
    }
}



