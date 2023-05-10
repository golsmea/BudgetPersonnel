package bgpersonnel.budget.objectif;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectifRepository extends JpaRepository<Objectif, Long> {
    Objectif findByName(String name);

    List<Objectif> findByUser(Long id);


    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.objectif = :objectif")
    Double calculateProgress(@Param("objectif") Objectif objectif);

}
