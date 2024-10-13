package vn.ptit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ptit.model.Esp32Config;

public interface Esp32ConfigRepository extends JpaRepository<Esp32Config, Integer>, Esp32ConfigRepositoryCustom {

}
