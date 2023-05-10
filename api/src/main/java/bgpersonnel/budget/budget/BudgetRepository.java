package bgpersonnel.budget.budget;

import bgpersonnel.budget.BudgetApplication;
import bgpersonnel.budget.objectif.Objectif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>{

    @Query(value = "SELECT SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction = 'EPARGNE' OR t.typeTransaction = 'DEPENSE' THEN t.amount ELSE 0 END) "
            + "FROM Transaction t "
            + "WHERE YEAR(t.dateTransaction) = :year "
            + "AND t.user.id = :userId")
    Double calculateAnnualGlobalBudgetForYear(Integer year, Long userId);


    @Query(value = "SELECT SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction = 'EPARGNE' OR t.typeTransaction = 'DEPENSE' THEN t.amount ELSE 0 END) "
            + "FROM Transaction t "
            + "WHERE t.user.id = :userId")
    Double calculateGlobalBudget(Long userId);




    @Query(value = "SELECT YEAR(t.dateTransaction) as year, SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction IN ('EPARGNE', 'DEPENSE') THEN t.amount ELSE 0 END) as totalBudget "
            + "FROM Transaction t "
            + "WHERE t.user.id = :userId "
            + "GROUP BY YEAR(t.dateTransaction)")
    List<Map<String, Object>> getTotalAnnualBudgetsByYearAndUser(@Param("userId") Long userId);


    @Query("SELECT MONTH(t.dateTransaction), YEAR(t.dateTransaction) as year, SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction = 'EPARGNE' OR t.typeTransaction = 'DEPENSE' THEN t.amount ELSE 0 END)"
            + " FROM Transaction t WHERE YEAR(t.dateTransaction) = :year AND t.user.id = :userId GROUP BY MONTH(t.dateTransaction)")
    List<Map<String, Object>> calculateMonthlyGlobalBudgetForYear(Integer year, Long userId);


    @Query(value = "SELECT COALESCE(SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END), 0) - COALESCE(SUM(CASE WHEN t.typeTransaction = 'EPARGNE' OR t.typeTransaction = 'DEPENSE' THEN t.amount ELSE 0 END), 0) " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId AND MONTH(t.dateTransaction) = :month AND YEAR(t.dateTransaction) = :year")
    Double calculateMonthlyGlobalBudgetForYearAndMonth(Long userId, Integer month, Integer year);


    @Query(value= "SELECT COALESCE(SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END), 0) - COALESCE(SUM(CASE WHEN t.typeTransaction = 'EPARGNE' OR t.typeTransaction = 'DEPENSE' THEN t.amount ELSE 0 END), 0) " +
            "FROM Transaction t " +
            "JOIN Category c ON t.category.id = c.id " +
            "JOIN Budget b ON c.id = b.category.id " +
            "WHERE t.user.id = :userId AND MONTH(t.dateTransaction) = :month AND YEAR(t.dateTransaction) = :year AND b.id = :budgetId")
    Double calculateMonthlyBudgetForYearAndMonthAndCategory(Long userId, Integer month, Integer year, Long budgetId);



    @Query("SELECT MONTH(t.dateTransaction), YEAR(t.dateTransaction) as year, SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction = 'EPARGNE' OR t.typeTransaction = 'DEPENSE' THEN t.amount ELSE 0 END)"
            + " FROM Transaction t " +
            " JOIN Category c ON t.category.id = c.id " +
            " JOIN Budget b ON c.id = b.category.id  " +
             "WHERE YEAR(t.dateTransaction) = :year AND t.user.id = :userId AND b.id = :budgetId GROUP BY MONTH(t.dateTransaction)")
    List<Map<String, Object>> calculateMonthlyBudgetForYearAndCategory(Integer year, Long userId, Long budgetId);

    @Query(value = "SELECT YEAR(t.dateTransaction) as year, SUM(CASE WHEN t.typeTransaction = 'REVENU' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction IN ('EPARGNE', 'DEPENSE') THEN t.amount ELSE 0 END) as totalBudget "
            + "FROM Transaction t "
            +" JOIN Category c ON t.category.id = c.id "
            +" JOIN Budget b ON c.id = b.category.id  "
            + "WHERE t.user.id = :userId "
            + "AND b.id = :budgetId "
            + "GROUP BY YEAR(t.dateTransaction)")
    List<Map<String, Object>> getTotalAnnualBudgetsByYearAndUserAndCategory(Long userId, Long budgetId);


    @Query(value = "SELECT SUM(CASE WHEN t.typeTransaction = 'REVENUE' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction = 'EXPENSE' OR t.typeTransaction = 'SAVING' THEN t.amount ELSE 0 END) "
            + "FROM Transaction t "
            +" JOIN Category c ON t.category.id = c.id "
            +" JOIN Budget b ON c.id = b.category.id  "
            + "WHERE YEAR(t.dateTransaction) = :year "
            + "AND b.id = :budgetId "
            + "AND t.user.id = :userId")
    Double calculateAnnualBudgetForYearAndCategory(Integer year, Long userId, Long budgetId);

    @Query(value = "SELECT SUM(CASE WHEN t.typeTransaction = 'REVENUE' THEN t.amount ELSE 0 END) - SUM(CASE WHEN t.typeTransaction = 'EXPENSE' OR t.typeTransaction = 'SAVING' THEN t.amount ELSE 0 END) "
            + "FROM Transaction t "
            +" JOIN Category c ON t.category.id = c.id "
            +" JOIN Budget b ON c.id = b.category.id  "
            + "WHERE b.id = :budgetId "
            + "AND t.user.id = :userId")
    Double calculateBudgetForCategory( Long userId, Long budgetId);






}
