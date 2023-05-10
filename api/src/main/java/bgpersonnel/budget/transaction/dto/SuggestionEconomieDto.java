package bgpersonnel.budget.transaction.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SuggestionEconomieDto {

    private String name;
    private double depense;
    private double depenseTotal;
    private double percentage;
    private double percentagePerCategory;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
