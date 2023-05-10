package bgpersonnel.budget.transaction;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserAndDateTransactionBetween(User user, LocalDateTime dateDebut, LocalDateTime dateFin);

    List<Transaction> findByCategoryAndUser(Category category, User user);

    List<Transaction> findByCategoryAndDateTransactionBetween(Category category, LocalDateTime dateDebut, LocalDateTime dateFin);

    List<Transaction> findByDateTransactionAndUser(LocalDateTime localDateTime, User user);

    /**
     * Cette méthode permet de récupérer les transactions d'un utilisateur entre deux dates et d'une catégorie
     *
     * @param userId     l'id de l'utilisateur
     * @param startDate  la date de début
     * @param endDate    la date de fin
     * @param categoryId l'id de la catégorie
     * @return la liste des transactions
     */
    @Query("SELECT new bgpersonnel.budget.transaction.TransactionReportDto("
            + "c.name, t.dateTransaction, t.amount, t.description, "
            + "t.typeTransaction) "
            + "FROM Transaction t "
            + "LEFT JOIN t.category c "
            + "WHERE t.user.id = :userId "
            + "AND t.dateTransaction BETWEEN :startDate AND :endDate "
            + "AND (:categoryId IS NULL OR t.category.id = :categoryId)")
    List<TransactionReportDto> getTransactionsByUserBetweenDatesAndCategory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId);
}
