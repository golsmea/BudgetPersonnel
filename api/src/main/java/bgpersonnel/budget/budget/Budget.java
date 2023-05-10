package bgpersonnel.budget.budget;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.category.Category;
import bgpersonnel.budget.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Budget extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double maxAmount;
    private boolean isGlobal;

    private boolean isEssential;

    @Enumerated(EnumType.STRING)
    private BudgetType type;

    @OneToOne
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
