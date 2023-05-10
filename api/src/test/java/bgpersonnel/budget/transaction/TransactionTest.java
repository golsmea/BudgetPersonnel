package bgpersonnel.budget.transaction;

import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.services.UserService;
import bgpersonnel.budget.budget.BudgetService;
import bgpersonnel.budget.category.Category;
import bgpersonnel.budget.category.CategoryService;
import bgpersonnel.budget.objectif.ObjectifService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class TransactionTest {

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
    @DisplayName("Création d'un transaction")
    public void createTransactionTest() {

        when(userService.getConnectedUser()).thenReturn(createUser());
        transactionService.create(transaction);

        assertEquals(1L, transaction.getUser().getId());
        assertNotNull(transaction.getCreatedAt());
        assertNotNull(transaction.getCreatedBy());
    }

    @Test
    @DisplayName("Modification d'une transaction")
    public void updateTransactionTest() {
        User user = createUser();
        transaction.setUser(user);

        when(userService.getConnectedUser()).thenReturn(user);
        transactionService.update(transaction);

        assertNotNull(transaction.getUpdatedAt());
        assertNotNull(transaction.getUpdatedBy());
    }

    @Test
    @DisplayName("Doit retourner une transaction avec une catégorie")
    public void addCategoryTest() {
        Category category = new Category();
        category.setId(1L);
        category.setName("CategorieTest");
        when(categoryService.findById(1L)).thenReturn(category);

        transactionService.addCategory(1L, 1L);
        assertEquals("CategorieTest", transaction.getCategory().getName());
        assertEquals(1L, transaction.getCategory().getId());
        assertEquals(TypeTransaction.DEPENSE, transaction.getTypeTransaction());
        assertEquals("Dépense", transaction.getTypeTransaction().getName());
    }
}
