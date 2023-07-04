package engine.persistence;

import engine.business.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}