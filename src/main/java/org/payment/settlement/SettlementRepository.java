package org.payment.settlement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.payment.consVar.DBQuery;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@ApplicationScoped
public class SettlementRepository {

    @Inject
    DataSource dataSource;

    public void insertSettlement(Settlement settlement) throws SQLException,RuntimeException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DBQuery.INSERT_SETTLEMENT_QUERY)) {
                ps.setObject(1, settlement.getSettlementId());
                ps.setObject(2, settlement.getReference());
                ps.setString(3, settlement.getFromActorId());
                ps.setString(4, settlement.getToActorId());
                ps.setBigDecimal(5, new BigDecimal(settlement.getAmount()));
                ps.setObject(6, settlement.getStatus().name());
                ps.executeUpdate();
        }  catch (SQLException e) {
            throw new RuntimeException("Error inserting settlement: " + settlement, e);
        }
    }
}
