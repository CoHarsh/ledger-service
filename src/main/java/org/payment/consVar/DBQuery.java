package org.payment.consVar;

public class DBQuery {
    public static final String INSERT_LEDGER_EVENT_QUERY = """
        INSERT INTO ledger.ledger_event
        (event_id, timestamp, actor_id, bucket, direction, amount, reference, signature, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

    public static final String LEDGER_EVENT_EXISTS_QUERY = """
        SELECT 1 FROM ledger.ledger_event
        WHERE reference = ?
        """;

    public static final String LEDGER_EVENT_BALANCE_QUERY = """
        SELECT COALESCE(
            SUM(CASE WHEN direction = 'CREDIT' THEN amount ELSE -amount END),
            0
        )
        FROM ledger.ledger_event
        WHERE actor_id = ? AND bucket = ?
        """;

    public static final String INSERT_SETTLEMENT_QUERY = """
        INSERT INTO ledger.settlement
        (settlement_id, reference_pay_event, from_actor, to_actor, amount, status, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;

    public static final String LOCK_SETTLEMENT_BATCH_QUERY = """
        UPDATE ledger.settlement
        SET status = 'PROCESSING', updated_at = CURRENT_TIMESTAMP
        WHERE settlement_id IN (
            SELECT settlement_id
            FROM ledger.settlement
            WHERE status = 'PENDING'
            ORDER BY created_at
            LIMIT ?
            FOR UPDATE SKIP LOCKED
        )
        RETURNING settlement_id, reference_pay_event, from_actor, to_actor, amount, status, created_at, updated_at
        """;
    public static final String UPDATE_SETTLEMENT_STATUS_QUERY = """
        UPDATE ledger.settlement
        SET status = ?, updated_at = CURRENT_TIMESTAMP
        WHERE settlement_id = ANY ( ? )
        """;
}
