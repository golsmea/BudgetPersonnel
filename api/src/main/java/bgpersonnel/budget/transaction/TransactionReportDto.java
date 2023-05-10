package bgpersonnel.budget.transaction;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionReportDto {
    private String name;
    private LocalDate date;
    private double amount;
    private String description;
    private String typeTransaction;

    public TransactionReportDto(String name, LocalDate date, double amount, String description, TypeTransaction typeTransaction) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.typeTransaction = typeTransaction.getName();
    }
}
