package bgpersonnel.budget.transaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SumTransactionResponse {

    private String name;

    private double sumDepense;

    private double sumRevenue;

    private double total;

    private int nbTransactions;

    private String dateDebut;

    private String dateFin;
}
