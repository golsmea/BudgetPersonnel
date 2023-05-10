package bgpersonnel.budget.category;

import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.authentification.common.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CategoryTest {

    @Mock
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        UserService userService = Mockito.mock(UserService.class);
        user = createUser();
        when(userService.getConnectedUser()).thenReturn(user);
        categoryService = new CategoryService(categoryRepository, userService);
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
    @DisplayName("Création d'une catégorie")
    public void createTransactionTest() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");

        categoryService.create(category);

        assertEquals(1L, category.getUser().getId());
        assertEquals(1L, category.getId());
        assertEquals("Test", category.getName());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getCreatedBy());
    }


    @Test
    @DisplayName("modification d'une catégorie")
    public void updateTransactionTest() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test");
        category.setUser(user);

        categoryService.update(category);

        assertNotNull(category.getUpdatedAt());
        assertEquals(category.getUpdatedBy(), user.getName());
    }
}
