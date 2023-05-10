package bgpersonnel.budget;


import bgpersonnel.budget.authentification.common.entity.ERole;
import bgpersonnel.budget.authentification.common.entity.Role;
import bgpersonnel.budget.authentification.common.entity.User;
import bgpersonnel.budget.category.Category;
import bgpersonnel.budget.transaction.Transaction;
import bgpersonnel.budget.transaction.TypeTransaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class InitDb implements CommandLineRunner {
    PasswordEncoder encoder;
    @PersistenceContext
    private EntityManager entityManager;

    public InitDb(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_USER);
        entityManager.persist(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setName(ERole.ROLE_ADMIN);
        entityManager.persist(roleAdmin);

        Role roleModerator = new Role();
        roleModerator.setName(ERole.ROLE_MODERATOR);
        entityManager.persist(roleModerator);

        // Users
        String password = encoder.encode("password");

        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user@mail.com");
        user1.setPassword(password);
        user1.getRoles().add(roleUser);
        entityManager.persist(user1);

        //Category
        Category category1 = new Category();
        category1.setName("category1");
        category1.setUser(user1);
        category1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(category1);

        Category category2 = new Category();
        category2.setName("category2");
        category2.setUser(user1);
        category2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(category2);

        //Transactions
        Transaction transaction = new Transaction();
        transaction.setAmount(275.0);
        transaction.setDescription("Transaction test unitaire");
        transaction.setTypeTransaction(TypeTransaction.DEPENSE);
        transaction.setUser(user1);
        transaction.setCategory(category1);
        transaction.setDateTransaction(LocalDate.of(2022, 01, 01));
        transaction.setCreatedAt(LocalDateTime.now());
        entityManager.persist(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(275.0);
        transaction2.setDescription("Transaction test 2");
        transaction2.setTypeTransaction(TypeTransaction.REVENU);
        transaction2.setUser(user1);
        transaction2.setCategory(category1);
        transaction2.setDateTransaction(LocalDate.of(2023, 01, 01));
        transaction2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setAmount(275.0);
        transaction3.setDescription("Transaction test 3");
        transaction3.setTypeTransaction(TypeTransaction.REVENU);
        transaction3.setUser(user1);
        transaction3.setCategory(category2);
        transaction3.setDateTransaction(LocalDate.of(2023, 02, 01));
        transaction3.setCreatedAt(LocalDateTime.now());
        entityManager.persist(transaction3);

        entityManager.flush();
    }
}
