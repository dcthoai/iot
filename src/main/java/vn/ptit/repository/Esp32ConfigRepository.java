package vn.ptit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ptit.model.Esp32Config;

@Repository
public interface Esp32ConfigRepository extends JpaRepository<Esp32Config, Integer> {
    // This class is marked as a repository, may not need a body
}
