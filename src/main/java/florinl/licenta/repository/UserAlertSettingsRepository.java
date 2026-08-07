package florinl.licenta.repository;

import florinl.licenta.domain.UserAlertSettings;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserAlertSettings entity.
 */
@Repository
public interface UserAlertSettingsRepository extends JpaRepository<UserAlertSettings, Long> {
    @Query("select userAlertSettings from UserAlertSettings userAlertSettings where userAlertSettings.user.login = ?#{authentication.name}")
    List<UserAlertSettings> findByUserIsCurrentUser();

    // new method for pageable data isolation
    @Query("select userAlertSettings from UserAlertSettings userAlertSettings where userAlertSettings.user.login = ?#{authentication.name}")
    Page<UserAlertSettings> findByUserIsCurrentUser(Pageable pageable);

    default Optional<UserAlertSettings> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserAlertSettings> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserAlertSettings> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select userAlertSettings from UserAlertSettings userAlertSettings left join fetch userAlertSettings.user",
        countQuery = "select count(userAlertSettings) from UserAlertSettings userAlertSettings"
    )
    Page<UserAlertSettings> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userAlertSettings from UserAlertSettings userAlertSettings left join fetch userAlertSettings.user")
    List<UserAlertSettings> findAllWithToOneRelationships();

    @Query(
        "select userAlertSettings from UserAlertSettings userAlertSettings left join fetch userAlertSettings.user where userAlertSettings.id =:id"
    )
    Optional<UserAlertSettings> findOneWithToOneRelationships(@Param("id") Long id);
}
