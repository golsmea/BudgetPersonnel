package bgpersonnel.budget.reporting;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor

public class ReportRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private ETypeReport reportType;
    private Long categoryId;
}
