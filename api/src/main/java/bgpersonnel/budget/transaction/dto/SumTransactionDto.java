package bgpersonnel.budget.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class SumTransactionDto {

    private String name;

    private double sumDepense;

    private double sumRevenue;

    private double total;

    private int nbTransactions;

    private String dateDebut;

    private String dateFin;
}
