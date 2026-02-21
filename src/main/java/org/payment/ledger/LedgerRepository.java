package org.payment.ledger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.payment.consVar.ConstantEnum.Bucket;
import org.payment.consVar.DBQuery;
import org.payment.util.CurrencyConverter;
import org.payment.util.CurrencyFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LedgerRepository {

    @Inject
    DataSource dataSource;

    // ---------- INSERT ----------

    public void insertLedgerEvents(List<LedgerEvent> ledgerEvents)
            throws SQLException {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(DBQuery.INSERT_LEDGER_EVENT_QUERY)) {
            CurrencyConverter converter = CurrencyFactory.defaultCurrency(); //INR only
            for (LedgerEvent event : ledgerEvents) {

                ps.setObject(1, event.eventId);
                ps.setTimestamp(2, Timestamp.from(event.timestamp));
                ps.setString(3, event.actorId);
                ps.setString(4, event.bucket.name());
                ps.setString(5, event.direction.name());
                ps.setBigDecimal(6, new BigDecimal(event.getAmount()));
                ps.setObject(7, event.reference);
                ps.setString(8, event.signature);

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    // ---------- EXISTS ----------

    public boolean eventExists(UUID payEventId)
            throws SQLException {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(DBQuery.LEDGER_EVENT_EXISTS_QUERY)) {

            ps.setObject(1, payEventId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ---------- BALANCE ----------

    public long getBalance(String actorId, Bucket bucket)
            throws SQLException {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(DBQuery.LEDGER_EVENT_BALANCE_QUERY)) {

            ps.setString(1, actorId);
            ps.setString(2, bucket.name());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }
}
