package sia.sever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Add custom querying if needed...
    User findByEmail(String email);
    User findByUserName(String username);
}
