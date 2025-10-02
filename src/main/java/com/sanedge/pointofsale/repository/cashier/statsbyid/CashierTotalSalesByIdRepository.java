package com.sanedge.pointofsale.repository.cashier.statsbyid;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.cashier.CashierMonthTotalSales;
import com.sanedge.pointofsale.models.cashier.CashierYearTotalSales;

@Repository
public interface CashierTotalSalesByIdRepository extends JpaRepository<Cashier, Long> {
    @Query(value = """
            WITH monthly_totals AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::TEXT AS year,
                    EXTRACT(MONTH FROM o.created_at)::INTEGER AS month,
                    COALESCE(SUM(o.total_price), 0)::BIGINT AS totalSales
                FROM orders o
                JOIN cashiers c ON o.cashier_id = c.cashier_id
                WHERE o.deleted_at IS NULL
                  AND c.deleted_at IS NULL
                  AND c.cashier_id = :cashierId
                  AND (
                      (EXTRACT(YEAR FROM o.created_at) = :startYear AND EXTRACT(MONTH FROM o.created_at) = :startMonth)
                      OR (EXTRACT(YEAR FROM o.created_at) = :endYear AND EXTRACT(MONTH FROM o.created_at) = :endMonth)
                  )
                GROUP BY EXTRACT(YEAR FROM o.created_at), EXTRACT(MONTH FROM o.created_at)
            ),
            all_months AS (
                SELECT :startYear::TEXT AS year, :startMonth AS month
                UNION
                SELECT :endYear::TEXT AS year, :endMonth AS month
            )
            SELECT
                am.year,
                TO_CHAR(TO_DATE(am.month::TEXT, 'MM'), 'FMMonth') AS month,
                COALESCE(mt.totalSales, 0)::BIGINT AS totalSales
            FROM all_months am
            LEFT JOIN monthly_totals mt ON am.year = mt.year AND am.month = mt.month
            ORDER BY am.year::INT DESC, am.month DESC
            """, nativeQuery = true)
    List<CashierMonthTotalSales> findMonthTotalSalesById(
            @Param("cashierId") Long cashierId,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::INTEGER AS year,
                    COALESCE(SUM(o.total_price), 0)::BIGINT AS totalSales
                FROM orders o
                JOIN cashiers c ON o.cashier_id = c.cashier_id
                WHERE o.deleted_at IS NULL
                  AND c.deleted_at IS NULL
                  AND c.cashier_id = :cashierId
                  AND EXTRACT(YEAR FROM o.created_at) IN (:year, :yearMinusOne)
                GROUP BY EXTRACT(YEAR FROM o.created_at)
            ),
            all_years AS (
                SELECT :year AS year
                UNION
                SELECT :yearMinusOne AS year
            )
            SELECT
                a.year::TEXT AS year,
                COALESCE(yd.totalSales, 0)::BIGINT AS totalSales
            FROM all_years a
            LEFT JOIN yearly_data yd ON a.year = yd.year
            ORDER BY a.year DESC
            """, nativeQuery = true)
    List<CashierYearTotalSales> findYearTotalSalesById(
            @Param("cashierId") Long cashierId,
            @Param("year") Integer year,
            @Param("yearMinusOne") Integer yearMinusOne);
}
