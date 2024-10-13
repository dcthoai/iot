package vn.ptit.repository;

import vn.ptit.model.Sensor;

import java.util.List;

public interface SensorRepositoryCustom {

    List<Sensor> getSensorDataByDate(String fromDate, String toDate);
}
