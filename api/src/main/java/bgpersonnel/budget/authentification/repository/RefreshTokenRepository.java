package bgpersonnel.budget.authentification.repository;

import java.util.Optional;

import bgpersonnel.budget.authentification.entity.RefreshToken;
import bgpersonnel.budget.authentification.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}