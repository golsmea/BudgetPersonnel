package bgpersonnel.budget.transaction;

import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.services.UserService;
import bgpersonnel.budget.budget.Budget;
import bgpersonnel.budget.budget.BudgetService;
import bgpersonnel.budget.category.Category;
import bgpersonnel.budget.category.CategoryService;
import bgpersonnel.budget.objectif.Objectif;
import bgpersonnel.budget.objectif.ObjectifService;
import bgpersonnel.budget.transaction.dto.SuggestionEconomieDto;
import bgpersonnel.budget.transaction.dto.SumTransactionDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ObjectifService objectifService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final BudgetService budgetService;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserService userService,
            CategoryService categoryService,
            ObjectifService objectifService,
            BudgetService budgetService
    ) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.objectifService = objectifService;
        this.budgetService = budgetService;
    }

    /**
     * Retourne la liste des transactions.
     * @return la liste de toutes les transactions.
     */
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    /**
     * Sauvegarde une nouvelle transaction dans la base de données en fonction du user
     * @param transaction à sauvegarder
     * @return transaction sauvegarder avec son id.
     */
    public Transaction create(Transaction transaction) {
        User user = this.userService.getConnectedUser();

        transaction.setUser(user);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCreatedBy(user.getName());

        Transaction savedTransaction = transactionRepository.save(transaction);
        Objectif objectif = transaction.getObjectif();
        if (objectif != null) {
            boolean objectifAtteint = objectifService.isObjectifAtteint(objectif.getId());
        }
        Budget budget = transaction.getCategory().getBudget();
        if (budget != null) {
            boolean budgetDepasse = budgetService.isBudgetDepasse(budget.getId());
        }
        return savedTransaction;
    }

    /**
     * Remplace les données d'une transaction dans la base de données en fonction de son id.
     * @param transaction nouvelles données à sauvegarder
     * @return la transaction avec les nouvelles données.
     */
    public Transaction update(Transaction transaction){
        User user = this.userService.getConnectedUser();
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setUpdatedBy(user.getName());
        return this.transactionRepository.save(transaction);
    }


    /**
     * Recherche une transaction en fonction de son id.
     * @param id de la transaction à rechercher
     * @return la transaction portant l'id passé en paramètre
     * @throws org.springframework.web.server.ResponseStatusException si aucune transaction ne porte cet id
     */
    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    /**
     * Recherche une transaction en fonction de sa catégorie.
     * @param id de la catégorie à rechercher
     * @return les transactions portant l'id de la catégorie passé en paramètre
     */
    public List<Transaction> findByCategory(Long id) {
        Category category = categoryService.findById(id);
        return transactionRepository.findByCategoryAndUser(category, this.userService.getConnectedUser());
    }


    /**
     * Recherche une transaction en fonction de la date de transaction.
     * @param strDateTime date de la transaction à rechercher
     * @return les transactions se situant à la date passé en paramètre
     */
    public List<Transaction> findByDate(String strDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.parse(strDateTime, formatter);

        return transactionRepository.findByDateTransactionAndUser(dateTime, this.userService.getConnectedUser());
    }

    /**
     * supprime une transaction en fonction de son id.
     * @param id de la transaction à supprimer.
     */
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    /**
     * Ajoute une catégorie à la transaction
     * @param transactionId  transaction à modifier
     * @param categoryId catégorie à ajouter à la transaction
     * @return la transaction avec les nouvelles données.
     */
    public Transaction addCategory(long transactionId, long categoryId) {
        Category category = categoryService.findById(categoryId);
        Transaction transaction = findById(transactionId);

        transaction.setCategory(category);

        return this.transactionRepository.save(transaction);
    }

    /**
     * Retourne une liste de catégorie ou les dépenses sont trop importnates par rapport aux autres catégorie.
     */
    public List<SuggestionEconomieDto> getSuggestionEconomie() {
        Year year = Year.now();
        User user = this.userService.getConnectedUser();
        int nbCategories = this.categoryService.countByUser(user);
        List<SuggestionEconomieDto> suggestionEconomieDtos = new ArrayList<>();

        LocalDateTime dateDebut = LocalDateTime.of(year.atDay(1), LocalTime.of(0, 0));
        LocalDateTime dateFin = LocalDateTime.of(year.atMonth(12).atEndOfMonth(), LocalTime.of(0, 0));

        List<Transaction> transactions = transactionRepository.findByUserAndDateTransactionBetween(
                user,
                dateDebut,
                dateFin
        );

        double depenseTotal = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getTypeTransaction() == TypeTransaction.DEPENSE) {
                depenseTotal += transaction.getAmount();
            }
        }

        if (nbCategories > 0 && depenseTotal > 0.0) {
            // Chaque catégorie doit être dans ce pourcentage, si pourcentage trop important
            // pour une catégorie, lévé une alerte pour signifier que des économies peuvent être fait sur cette catégorie.
            // Cela montrera qu'il y a trop de dépences dans une catégorie.
            double percentagePerCategory = 100.0 / nbCategories;
            List<SumTransactionDto> sumTransactionDtos = getSumTransactionByCategoriesAndYear(year);

            for (SumTransactionDto sumTransactionDto : sumTransactionDtos) {
                double percentage = sumTransactionDto.getSumDepense() * 100 / depenseTotal;

                if (percentage > percentagePerCategory + 5) {
                    // Alerter sur cette catégorie.
                    SuggestionEconomieDto suggestionEconomieDto = new SuggestionEconomieDto();
                    suggestionEconomieDto.setName(sumTransactionDto.getName());
                    suggestionEconomieDto.setDepense(sumTransactionDto.getSumDepense());
                    suggestionEconomieDto.setDepenseTotal(depenseTotal);
                    suggestionEconomieDto.setPercentage(percentage);
                    suggestionEconomieDto.setPercentagePerCategory(percentagePerCategory);
                    suggestionEconomieDto.setDateDebut(dateDebut);
                    suggestionEconomieDto.setDateFin(dateFin);

                    suggestionEconomieDtos.add(suggestionEconomieDto);
                }
            }
        }
        return suggestionEconomieDtos;
    }

    public List<SumTransactionDto> getSumTransactionByCategoriesAndYear(Year year) {
        User user = this.userService.getConnectedUser();
        List<Category> categories = categoryService.findByUser(user);

        LocalDateTime dateDebut = LocalDateTime.of(year.atDay(1), LocalTime.of(0, 0));
        LocalDateTime dateFin = LocalDateTime.of(year.atMonth(12).atEndOfMonth(), LocalTime.of(0, 0));

        return getSumOfTransactionsByDate(categories, dateDebut, dateFin);
    }

    public List<SumTransactionDto> getSumTransactionByCategoriesAndMonth(YearMonth yearMonth) {
        User user = this.userService.getConnectedUser();
        List<Category> categories = categoryService.findByUser(user);

        LocalDateTime dateDebut = LocalDateTime.of(yearMonth.atDay(1), LocalTime.of(0, 0));
        LocalDateTime dateFin = LocalDateTime.of(yearMonth.atEndOfMonth(), LocalTime.of(0, 0));

        return getSumOfTransactionsByDate(categories, dateDebut, dateFin);
    }

    private List<SumTransactionDto> getSumOfTransactionsByDate(
            List<Category> categories,
            LocalDateTime dateDebut,
            LocalDateTime dateFin
    ) {
        List<SumTransactionDto> result = new ArrayList<>();
        for (Category category: categories) {
            List<Transaction> transactions =
                    transactionRepository.findByCategoryAndDateTransactionBetween(category, dateDebut, dateFin);
            double depense = 0.0;
            double revenu = 0.0;

            for (Transaction transaction : transactions) {
                if (transaction.getTypeTransaction() == TypeTransaction.DEPENSE) {
                    depense += transaction.getAmount();
                } else {
                    revenu += transaction.getAmount();
                }
            }

            result.add(new SumTransactionDto(
                    category.getName(),
                    depense,
                    revenu,
                    (revenu - depense),
                    transactions.size(),
                    dateDebut.format(formatter),
                    dateFin.format(formatter)
            ));
        }

        result.sort(Comparator.comparingDouble(SumTransactionDto::getTotal));

        return result;
    }
}
