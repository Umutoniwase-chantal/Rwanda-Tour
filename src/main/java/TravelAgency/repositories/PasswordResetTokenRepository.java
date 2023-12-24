package TravelAgency.repositories;

import TravelAgency.entities.PasswordResetToken;
import TravelAgency.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.expiryDate <= CURRENT_TIMESTAMP")

    List<PasswordResetToken> findExpiredTokens();

    PasswordResetToken findByUser(User user);



}
