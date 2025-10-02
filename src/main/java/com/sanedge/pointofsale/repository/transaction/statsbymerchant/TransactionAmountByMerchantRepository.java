package com.sanedge.pointofsale.repository.transaction.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.transaction.Transaction;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyAmountFailed;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyAmountSuccess;
import com.sanedge.pointofsale.models.transaction.TransactionYearlyAmountFailed;
import com.sanedge.pointofsale.models.transaction.TransactionYearlyAmountSuccess;

@Repository
public interface TransactionAmountByMerchantRepository extends JpaRepository<Transaction, Long> {
    @Query(value = """
            WITH monthly_data AS (
                SELECT
                    year::text AS year,
                    TO_CHAR(TO_DATE(month::text, 'MM'), 'Mon') AS month,
                    total_success::int AS totalSuccess,
                    total_amount::bigint AS totalAmount
                FROM monthly_data
                WHERE
                    t.deleted_at IS NULL
                    AND t.status = 'SUCCESS'
                    AND t.merchant_id = :merchantId
                    AND (
                        (t.created_at >= make_date(:year, :month, 1)
                         AND t.created_at < (make_date(:year, :month, 1) + interval '1 month'))
                        OR
                        (t.created_at >= make_date(:prevYear, :prevMonth, 1)
                         AND t.created_at < (make_date(:prevYear, :prevMonth, 1) + interval '1 month'))
                    )
                GROUP BY EXTRACT(YEAR FROM t.created_at), EXTRACT(MONTH FROM t.created_at)
            ),
            formatted_data AS (
                SELECT year::text,
                       TO_CHAR(TO_DATE(month::text, 'MM'), 'Mon') AS month,
                       total_success,
                       total_amount
                FROM monthly_data
                UNION ALL
                SELECT :year::text,
                       TO_CHAR(make_date(:year, :month, 1), 'Mon'),
                       0, 0
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE year = :year AND month = :month
                )
                UNION ALL
                SELECT :prevYear::text,
                       TO_CHAR(make_date(:prevYear, :prevMonth, 1), 'Mon'),
                       0, 0
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE year = :prevYear AND month = :prevMonth
                )
            )
            SELECT * FROM formatted_data
            ORDER BY year DESC, TO_DATE(month, 'Mon') DESC
            """, nativeQuery = true)
    List<TransactionMonthlyAmountSuccess> findMonthlySuccessByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("prevYear") Integer prevYear,
            @Param("prevMonth") Integer prevMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    year::text AS year,
                    total_success::int AS totalSuccess,
                    total_amount::bigint AS totalAmount
                FROM yearly_data
                WHERE
                    t.deleted_at IS NULL
                    AND t.status = 'SUCCESS'
                    AND t.merchant_id = :merchantId
                    AND (EXTRACT(YEAR FROM t.created_at) = :year
                         OR EXTRACT(YEAR FROM t.created_at) = :year - 1)
                GROUP BY EXTRACT(YEAR FROM t.created_at)
            ),
            formatted_data AS (
                SELECT year::text, total_success, total_amount FROM yearly_data
                UNION ALL
                SELECT :year::text, 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE year = :year)
                UNION ALL
                SELECT (:year - 1)::text, 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE year = :year - 1)
            )
            SELECT * FROM formatted_data
            ORDER BY year DESC
            """, nativeQuery = true)
    List<TransactionYearlyAmountSuccess> findYearlySuccessByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year);

    @Query(value = """
            WITH monthly_data AS (
                SELECT
                    EXTRACT(YEAR FROM t.created_at)::integer AS year,
                    EXTRACT(MONTH FROM t.created_at)::integer AS month,
                    COUNT(*) AS total_failed,
                    COALESCE(SUM(t.amount), 0)::BIGINT AS total_amount
                FROM transactions t
                WHERE
                    t.deleted_at IS NULL
                    AND t.status = 'FAILEd'
                    AND t.merchant_id = :merchantId
                    AND (
                        (t.created_at >= make_date(:year, :month, 1)
                         AND t.created_at < (make_date(:year, :month, 1) + interval '1 month'))
                        OR
                        (t.created_at >= make_date(:prevYear, :prevMonth, 1)
                         AND t.created_at < (make_date(:prevYear, :prevMonth, 1) + interval '1 month'))
                    )
                GROUP BY EXTRACT(YEAR FROM t.created_at), EXTRACT(MONTH FROM t.created_at)
            ),
            formatted_data AS (
                SELECT
                    year::text AS year,
                    TO_CHAR(TO_DATE(month::text, 'MM'), 'Mon') AS month,
                    total_failed::int AS totalFailed,
                    total_amount::bigint AS totalAmount
                FROM monthly_data
                UNION ALL
                SELECT :year::text,
                       TO_CHAR(make_date(:year, :month, 1), 'Mon'),
                       0, 0
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE year = :year AND month = :month
                )
                UNION ALL
                SELECT :prevYear::text,
                       TO_CHAR(make_date(:prevYear, :prevMonth, 1), 'Mon'),
                       0, 0
                WHERE NOT EXISTS (
                    SELECT 1 FROM monthly_data
                    WHERE year = :prevYear AND month = :prevMonth
                )
            )
            SELECT * FROM formatted_data
            ORDER BY year DESC, TO_DATE(month, 'Mon') DESC
            """, nativeQuery = true)
    List<TransactionMonthlyAmountFailed> findMonthlyFailedByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("prevYear") Integer prevYear,
            @Param("prevMonth") Integer prevMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    EXTRACT(YEAR FROM t.created_at)::integer AS year,
                    COUNT(*) AS total_failed,
                    COALESCE(SUM(t.amount), 0)::BIGINT AS total_amount
                FROM transactions t
                WHERE
                    t.deleted_at IS NULL
                    AND t.status = 'FAILED'
                    AND t.merchant_id = :merchantId
                    AND (EXTRACT(YEAR FROM t.created_at) = :year
                         OR EXTRACT(YEAR FROM t.created_at) = :year - 1)
                GROUP BY EXTRACT(YEAR FROM t.created_at)
            ),
            formatted_data AS (
                SELECT
                    year::text AS year,
                    total_failed::int AS totalFailed,
                    total_amount::bigint AS totalAmount
                FROM yearly_data
                UNION ALL
                SELECT :year::text, 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE year = :year)
                UNION ALL
                SELECT (:year - 1)::text, 0, 0 WHERE NOT EXISTS (SELECT 1 FROM yearly_data WHERE year = :year - 1)
            )
            SELECT * FROM formatted_data
            ORDER BY year DESC
            """, nativeQuery = true)
    List<TransactionYearlyAmountFailed> findYearlyFailedByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year);
}
