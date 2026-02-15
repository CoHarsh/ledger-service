package org.payment.settlement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.payment.constant.DBQuery;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@ApplicationScoped
public class SettlementRepository {

    @Inject
    DataSource dataSource;

    public void insertSettlement(Settlement settlement) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DBQuery.INSERT_SETTLEMENT_QUERY)) {
                ps.setObject(1, settlement.getSettlementId());
                ps.setObject(2, settlement.getReference());
                ps.setString(3, settlement.getFromActorId());
                ps.setString(4, settlement.getToActorId());
                ps.setBigDecimal(5, settlement.getAmount());
                ps.setObject(6, settlement.getStatus().name());
                ps.executeBatch();
        }
    }
}
