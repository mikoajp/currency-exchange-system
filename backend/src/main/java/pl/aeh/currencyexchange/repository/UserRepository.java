package pl.aeh.currencyexchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.aeh.currencyexchange.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     *
     * @param email user's email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user with given email exists
     *
     * @param email email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find user by email with wallets eagerly loaded
     *
     * @param email user's email
     * @return Optional containing user with wallets
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.wallets WHERE u.email = :email")
    Optional<User> findByEmailWithWallets(@Param("email") String email);

    /**
     * Find user by id with wallets eagerly loaded
     *
     * @param id user's id
     * @return Optional containing user with wallets
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.wallets WHERE u.id = :id")
    Optional<User> findByIdWithWallets(@Param("id") Long id);

    /**
     * Check if user is enabled
     *
     * @param email user's email
     * @return true if user is enabled, false otherwise
     */
    @Query("SELECT u.enabled FROM User u WHERE u.email = :email")
    Optional<Boolean> isUserEnabled(@Param("email") String email);
}
