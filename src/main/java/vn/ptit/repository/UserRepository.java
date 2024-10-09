package vn.ptit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ptit.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {

    Optional<User> findByUsername(String username);
}
