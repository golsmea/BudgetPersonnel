package bgpersonnel.budget.transaction;


import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.category.Category;
import bgpersonnel.budget.model.BaseEntity;
import bgpersonnel.budget.objectif.Objectif;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double amount;
    private String description;
    private LocalDate dateTransaction;
    private TypeTransaction typeTransaction;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Objectif objectif;

}
