package org.payment.settlement;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.payment.consVar.DBQuery;
import org.payment.consVar.settlement.SettlementStatusEnum;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SettlementRepository implements PanacheRepository<Settlement> {

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

    public void updateSettlementStatus(List<UUID> settlementIds, SettlementStatusEnum newStatus) throws SQLException,RuntimeException {
        if (settlementIds.isEmpty()) return;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(DBQuery.UPDATE_SETTLEMENT_STATUS_QUERY)) {
            ps.setString(1, newStatus.name());
            Array sqlArray = conn.createArrayOf("uuid", settlementIds.toArray());
            ps.setArray(2, sqlArray);
            ps.executeUpdate();
        }  catch (SQLException e) {
            throw new RuntimeException("Error updating settlement status for settlements: " + settlementIds, e);
        }
    }

    @Transactional
    public List<Settlement> lockBatch(int limit) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DBQuery.LOCK_SETTLEMENT_BATCH_QUERY)) {
            ps.setInt(1, limit);
            try (var rs = ps.executeQuery()) {
                List<Settlement> settlements = new java.util.ArrayList<>();
                while (rs.next()) {
                    Settlement s = Settlement.builder()
                            .settlementId(rs.getObject("settlement_id", UUID.class))
                            .reference(rs.getObject("reference_pay_event", UUID.class))
                            .fromActorId(rs.getString("from_actor"))
                            .toActorId(rs.getString("to_actor"))
                            .amount(rs.getBigDecimal("amount").toBigInteger())
                            .status(SettlementStatusEnum.valueOf(rs.getString("status")))
                            .createdAt(rs.getTimestamp("created_at"))
                            .updatedAt(rs.getTimestamp("updated_at"))
                            .build();
                    settlements.add(s);
                }
                return settlements;
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Error locking settlement batch", e);
        }
    }

}
