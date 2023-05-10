package bgpersonnel.budget.category;

import bgpersonnel.budget.authentification.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);

    int countByUser(User user);
}
