package bgpersonnel.budget.transaction;

import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.services.UserService;
import bgpersonnel.budget.budget.BudgetService;
import bgpersonnel.budget.category.Category;
import bgpersonnel.budget.category.CategoryService;
import bgpersonnel.budget.objectif.ObjectifService;
import bgpersonnel.budget.transaction.dto.SumTransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ListSumTransactionTest {

    Transaction transaction;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ObjectifService objectifService = Mockito.mock(ObjectifService.class);
        BudgetService budgetService = Mockito.mock(BudgetService.class);
        userService = Mockito.mock(UserService.class);
        categoryService = Mockito.mock(CategoryService.class);
        transactionService = new TransactionService(
                transactionRepository,
                userService,
                categoryService,
                objectifService,
                budgetService
        );
        this.transaction = new Transaction();
        this.transaction.setId(1L);
        this.transaction.setAmount(275.0);
        this.transaction.setDateTransaction(LocalDate.now());
        this.transaction.setTypeTransaction(TypeTransaction.DEPENSE);
        this.transaction.setDescription("Transaction test unitaire");

        Mockito.when(transactionRepository.findById(1L)).thenReturn(Optional.of(this.transaction));
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRoles(Set.of(new Role(1, ERole.ROLE_USER)));

        return user;
    }

    @Test
    public void getSumTransactionTest() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Categorie1");

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(75.5);
        transaction1.setCategory(category);
        transaction1.setTypeTransaction(TypeTransaction.REVENU);
        transaction1.setDateTransaction(LocalDate.of(2023, 04, 23));

        List<Transaction> transactionListCategory2023 = new ArrayList<>();
        transactionListCategory2023.add(transaction1);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Categorie2");

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(175.5);
        transaction2.setCategory(category);
        transaction2.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction2.setDateTransaction(LocalDate.of(2023, 03, 23));

        Transaction transaction3 = new Transaction();
        transaction3.setAmount(75.5);
        transaction3.setCategory(category);
        transaction3.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction3.setDateTransaction(LocalDate.of(2023, 04, 23));

        List<Transaction> transactionListCategory2Year2023 = new ArrayList<>();
        transactionListCategory2Year2023.add(transaction2);
        transactionListCategory2Year2023.add(transaction3);

        Category category3 = new Category();
        category3.setId(3L);
        category3.setName("Categorie3");


        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);
        categoryList.add(category2);
        categoryList.add(category3);

        LocalDateTime dateDebut = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime dateFin = LocalDateTime.of(2023, 12, 31, 0, 0);
        User user = createUser();

        when(userService.getConnectedUser()).thenReturn(user);
        when(categoryService.findByUser(user)).thenReturn(categoryList);

        when(transactionRepository.findByCategoryAndDateTransactionBetween(
                category,
                dateDebut,
                dateFin)
        ).thenReturn(transactionListCategory2023);


        when(transactionRepository.findByCategoryAndDateTransactionBetween(
                category2,
                dateDebut,
                dateFin)
        ).thenReturn(transactionListCategory2Year2023);


        when(transactionRepository.findByCategoryAndDateTransactionBetween(
                category3,
                dateDebut,
                dateFin)
        ).thenReturn(new ArrayList<>());

        List<SumTransactionDto> sumTransactionDtos = transactionService.getSumTransactionByCategoriesAndYear(Year.of(2023));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        assertEquals(sumTransactionDtos.get(0).getTotal(), -251.0);
        assertEquals(sumTransactionDtos.get(0).getSumRevenue(), 0);
        assertEquals(sumTransactionDtos.get(0).getSumDepense(), 251.0);
        assertEquals(sumTransactionDtos.get(0).getNbTransactions(), 2);
        assertEquals(sumTransactionDtos.get(0).getDateDebut(), dateDebut.format(formatter));
        assertEquals(sumTransactionDtos.get(0).getDateFin(), dateFin.format(formatter));
        assertEquals(sumTransactionDtos.get(0).getName(), "Categorie2");
        assertEquals(sumTransactionDtos.get(1).getTotal(), 0.0);
        assertEquals(sumTransactionDtos.get(1).getName(), "Categorie3");
        assertEquals(sumTransactionDtos.get(2).getTotal(), 75.5);
        assertEquals(sumTransactionDtos.get(2).getName(), "Categorie1");
    }
}
