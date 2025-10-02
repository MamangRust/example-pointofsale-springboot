package com.sanedge.pointofsale.repository.transaction.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.transaction.Transaction;
import com.sanedge.pointofsale.models.transaction.TransactionMonthlyMethod;
import com.sanedge.pointofsale.models.transaction.TransactionYearMethod;

@Repository
public interface TransactionMethodRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
                        WITH
                            date_ranges AS (
                                SELECT
                                    make_date(:year1, :month1, 1) AS range1_start,
                                    (make_date(:year1, :month1, 1) + interval '1 month') AS range1_end,
                                    make_date(:year2, :month2, 1) AS range2_start,
                                    (make_date(:year2, :month2, 1) + interval '1 month') AS range2_end
                            ),
                            payment_methods AS (
                                SELECT DISTINCT payment_method
                                FROM transactions
                                WHERE deleted_at IS NULL
                            ),
                            all_months AS (
                                SELECT generate_series(
                                    date_trunc('month', LEAST(
                                        (SELECT range1_start FROM date_ranges),
                                        (SELECT range2_start FROM date_ranges)
                                    )),
                                    date_trunc('month', GREATEST(
                                        (SELECT range1_end FROM date_ranges),
                                        (SELECT range2_end FROM date_ranges)
                                    ) - interval '1 day'),
                                    interval '1 month'
                                )::date AS activity_month
                            ),
                            all_combinations AS (
                                SELECT am.activity_month, pm.payment_method
                                FROM all_months am
                                CROSS JOIN payment_methods pm
                            ),
                            monthly_transactions AS (
                                SELECT
                                    date_trunc('month', t.created_at)::date AS activity_month,
                                    t.payment_method,
                                    COUNT(t.transaction_id) AS total_transactions,
                                    COALESCE(SUM(t.amount), 0)::BIGINT AS total_amount
                                FROM transactions t
                                JOIN date_ranges dr ON (
                                    t.created_at >= dr.range1_start AND t.created_at < dr.range1_end
                                    OR
                                    t.created_at >= dr.range2_start AND t.created_at < dr.range2_end
                                )
                                WHERE t.deleted_at IS NULL AND t.status = 'SUCCESS'
                                GROUP BY date_trunc('month', t.created_at), t.payment_method
                            )
            SELECT
                TO_CHAR(ac.activity_month, 'Mon') AS month,
                ac.payment_method AS paymentMethod,
                COALESCE(mt.total_transactions, 0)::int AS totalTransactions,
                COALESCE(mt.total_amount, 0)::bigint AS totalAmount
            FROM all_combinations ac
            LEFT JOIN monthly_transactions mt
                   ON ac.activity_month = mt.activity_month
                  AND ac.payment_method = mt.payment_method
            ORDER BY ac.activity_month, ac.payment_method
                        """, nativeQuery = true)
    List<TransactionMonthlyMethod> findMonthlyMethodsSuccess(
            @Param("year1") Integer year1,
            @Param("month1") Integer month1,
            @Param("year2") Integer year2,
            @Param("month2") Integer month2);

    @Query(value = """
            WITH
                date_ranges AS (
                    SELECT
                        make_date(:year1, :month1, 1) AS range1_start,
                        (make_date(:year1, :month1, 1) + interval '1 month') AS range1_end,
                        make_date(:year2, :month2, 1) AS range2_start,
                        (make_date(:year2, :month2, 1) + interval '1 month') AS range2_end
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_months AS (
                    SELECT generate_series(
                        date_trunc('month', LEAST(
                            (SELECT range1_start FROM date_ranges),
                            (SELECT range2_start FROM date_ranges)
                        )),
                        date_trunc('month', GREATEST(
                            (SELECT range1_end FROM date_ranges),
                            (SELECT range2_end FROM date_ranges)
                        ) - interval '1 day'),
                        interval '1 month'
                    )::date AS activity_month
                ),
                all_combinations AS (
                    SELECT am.activity_month, pm.payment_method
                    FROM all_months am
                    CROSS JOIN payment_methods pm
                ),
                monthly_transactions AS (
                    SELECT
                        date_trunc('month', t.created_at)::date AS activity_month,
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        COALESCE(SUM(t.amount), 0)::BIGINT AS total_amount
                    FROM transactions t
                    JOIN date_ranges dr ON (
                        t.created_at >= dr.range1_start AND t.created_at < dr.range1_end
                        OR
                        t.created_at >= dr.range2_start AND t.created_at < dr.range2_end
                    )
                    WHERE t.deleted_at IS NULL AND t.status = 'FAILED'
                    GROUP BY date_trunc('month', t.created_at), t.payment_method
                )
            SELECT
                TO_CHAR(ac.activity_month, 'Mon') AS month,
                ac.payment_method AS paymentMethod,
                COALESCE(mt.total_transactions, 0)::int AS totalTransactions,
                COALESCE(mt.total_amount, 0)::bigint AS totalAmount
            FROM all_combinations ac
            LEFT JOIN monthly_transactions mt
                ON ac.activity_month = mt.activity_month
                AND ac.payment_method = mt.payment_method
            ORDER BY ac.activity_month, ac.payment_method
            """, nativeQuery = true)
    List<TransactionMonthlyMethod> findMonthlyMethodsFailed(
            @Param("year1") Integer year1,
            @Param("month1") Integer month1,
            @Param("year2") Integer year2,
            @Param("month2") Integer month2);

    @Query(value = """
            WITH
                year_range AS (
                    SELECT
                        :year - 1 AS start_year,
                        :year AS end_year
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_years AS (
                    SELECT generate_series(
                        (SELECT start_year FROM year_range),
                        (SELECT end_year FROM year_range)
                    )::int AS year
                ),
                all_combinations AS (
                    SELECT ay.year::text AS year, pm.payment_method
                    FROM all_years ay
                    CROSS JOIN payment_methods pm
                ),
                yearly_transactions AS (
                    SELECT
                        EXTRACT(YEAR FROM t.created_at)::text AS year,
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        COALESCE(SUM(t.amount), 0)::BIGINT AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.status = 'SUCCESS'
                        AND EXTRACT(YEAR FROM t.created_at) BETWEEN (SELECT start_year FROM year_range) AND (SELECT end_year FROM year_range)
                    GROUP BY EXTRACT(YEAR FROM t.created_at), t.payment_method
                )
            SELECT
                ac.year AS year,
                ac.payment_method AS paymentMethod,
                COALESCE(yt.total_transactions, 0)::int AS totalTransactions,
                COALESCE(yt.total_amount, 0)::bigint AS totalAmount
            FROM all_combinations ac
            LEFT JOIN yearly_transactions yt
                ON ac.year = yt.year
                AND ac.payment_method = yt.payment_method
            ORDER BY ac.year, ac.payment_method
            """, nativeQuery = true)
    List<TransactionYearMethod> findYearlyMethodsSuccess(@Param("year") Integer year);

    @Query(value = """
            WITH
                year_range AS (
                    SELECT
                        :year - 1 AS start_year,
                        :year AS end_year
                ),
                payment_methods AS (
                    SELECT DISTINCT payment_method
                    FROM transactions
                    WHERE deleted_at IS NULL
                ),
                all_years AS (
                    SELECT generate_series(
                        (SELECT start_year FROM year_range),
                        (SELECT end_year FROM year_range)
                    )::int AS year
                ),
                all_combinations AS (
                    SELECT ay.year::text AS year, pm.payment_method
                    FROM all_years ay
                    CROSS JOIN payment_methods pm
                ),
                yearly_transactions AS (
                    SELECT
                        EXTRACT(YEAR FROM t.created_at)::text AS year,
                        t.payment_method,
                        COUNT(t.transaction_id) AS total_transactions,
                        COALESCE(SUM(t.amount), 0)::BIGINT AS total_amount
                    FROM transactions t
                    WHERE
                        t.deleted_at IS NULL
                        AND t.status = 'FAILED'
                        AND EXTRACT(YEAR FROM t.created_at) BETWEEN (SELECT start_year FROM year_range) AND (SELECT end_year FROM year_range)
                    GROUP BY EXTRACT(YEAR FROM t.created_at), t.payment_method
                )
            SELECT
                ac.year AS year,
                ac.payment_method AS paymentMethod,
                COALESCE(yt.total_transactions, 0)::int AS totalTransactions,
                COALESCE(yt.total_amount, 0)::bigint AS totalAmount
            FROM all_combinations ac
            LEFT JOIN yearly_transactions yt
                ON ac.year = yt.year
                AND ac.payment_method = yt.payment_method
            ORDER BY ac.year, ac.payment_method
            """, nativeQuery = true)
    List<TransactionYearMethod> findYearlyMethodsFailed(@Param("year") Integer year);
}
