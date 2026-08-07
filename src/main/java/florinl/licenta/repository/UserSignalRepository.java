package florinl.licenta.repository;

import florinl.licenta.domain.UserSignal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserSignal entity.
 */
@Repository
public interface UserSignalRepository extends JpaRepository<UserSignal, Long> {
    @Query("select userSignal from UserSignal userSignal where userSignal.user.login = ?#{authentication.name}")
    List<UserSignal> findByUserIsCurrentUser();

    // new method for pageable data isolation
    @Query("select userSignal from UserSignal userSignal where userSignal.user.login = ?#{authentication.name}")
    Page<UserSignal> findByUserIsCurrentUser(Pageable pageable);

    default Optional<UserSignal> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<UserSignal> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<UserSignal> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select userSignal from UserSignal userSignal left join fetch userSignal.user",
        countQuery = "select count(userSignal) from UserSignal userSignal"
    )
    Page<UserSignal> findAllWithToOneRelationships(Pageable pageable);

    @Query("select userSignal from UserSignal userSignal left join fetch userSignal.user")
    List<UserSignal> findAllWithToOneRelationships();

    @Query("select userSignal from UserSignal userSignal left join fetch userSignal.user where userSignal.id =:id")
    Optional<UserSignal> findOneWithToOneRelationships(@Param("id") Long id);
}
