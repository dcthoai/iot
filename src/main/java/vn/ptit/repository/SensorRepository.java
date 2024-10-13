package vn.ptit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ptit.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Integer>, SensorRepositoryCustom {

}
