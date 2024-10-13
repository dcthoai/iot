package vn.ptit.repository.impl;

import org.springframework.stereotype.Repository;
import vn.ptit.common.Common;
import vn.ptit.model.Sensor;
import vn.ptit.repository.SensorRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class SensorRepositoryImpl implements SensorRepositoryCustom {

    private final EntityManager entityManager;

    public SensorRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Sensor> getSensorDataByDate(String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("SELECT * FROM sensor");
        Map<String, Object> params = new HashMap<>();

        if (Objects.nonNull(fromDate) && Objects.nonNull(toDate)) {
            sql.append(" WHERE created_date BETWEEN :fromDate AND :toDate");
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
        } else if (Objects.nonNull(fromDate)) {
            sql.append(" WHERE created_date >= :fromDate");
            params.put("fromDate", fromDate);
        } else if (Objects.nonNull(toDate)) {
            sql.append(" WHERE created_date <= :toDate");
            params.put("toDate", toDate);
        }

        sql.append(" ORDER BY created_date DESC");

        Query query = entityManager.createNativeQuery(sql.toString(), Sensor.class);
        Common.setSqlParams(query, params);

        return query.getResultList();
    }
}
