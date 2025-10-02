package com.sanedge.pointofsale.repository.cashier.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.cashier.CashierMonthSales;
import com.sanedge.pointofsale.models.cashier.CashierYearSales;

@Repository
public interface CashierSalesRepository extends JpaRepository<Cashier, Long> {

    @Query(value = """
            WITH date_range AS (
                SELECT
                    make_date(:year, :startMonth, 1) AS start_date,
                    make_date(:year, :endMonth, 1)
                        + interval '1 month' * (1 + :endMonth - :startMonth)
                        - interval '1 day' AS end_date
            ),
            cashier_activity AS (
                SELECT
                    c.cashier_id AS cashierId,
                    c.name AS cashierName,
                    EXTRACT(MONTH FROM o.created_at) AS activityMonth,
                    COUNT(o.order_id) AS orderCount,
                    COALESCE(SUM(o.total_price), 0)::BIGINT AS totalSales
                FROM orders o
                JOIN cashiers c ON o.cashier_id = c.cashier_id
                WHERE o.deleted_at IS NULL
                  AND c.deleted_at IS NULL
                  AND o.created_at BETWEEN
                        (SELECT start_date FROM date_range)
                        AND (SELECT end_date FROM date_range)
                GROUP BY c.cashier_id, c.name, EXTRACT(MONTH FROM o.created_at)
            )
            SELECT
                ca.cashierId,
                ca.cashierName,
                TO_CHAR(TO_DATE(ca.activityMonth::TEXT, 'MM'), 'Mon') AS month,
                ca.orderCount,
                ca.totalSales
            FROM cashier_activity ca
            ORDER BY ca.activityMonth, ca.cashierId
            """, nativeQuery = true)
    List<CashierMonthSales> findMonthSales(
            @Param("year") Integer year,
            @Param("startMonth") Integer startMonth,
            @Param("endMonth") Integer endMonth);

    @Query(value = """
            WITH last_five_years AS (
                SELECT
                    c.cashier_id AS cashierId,
                    c.name AS cashierName,
                    EXTRACT(YEAR FROM o.created_at)::TEXT AS year,
                    COUNT(o.order_id) AS orderCount,
                    COALESCE(SUM(o.total_price), 0)::BIGINT AS totalSales
                FROM orders o
                JOIN cashiers c ON o.cashier_id = c.cashier_id
                WHERE o.deleted_at IS NULL
                  AND c.deleted_at IS NULL
                  AND EXTRACT(YEAR FROM o.created_at)
                        BETWEEN (:year - 4) AND :year
                GROUP BY c.cashier_id, c.name, EXTRACT(YEAR FROM o.created_at)
            )
            SELECT
                year,
                cashierId,
                cashierName,
                orderCount,
                totalSales
            FROM last_five_years
            ORDER BY year, cashierId
            """, nativeQuery = true)
    List<CashierYearSales> findYearSales(@Param("year") Integer year);
}
