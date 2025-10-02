package com.sanedge.pointofsale.repository.order.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.order.Order;
import com.sanedge.pointofsale.models.order.OrderMonthTotalRevenue;
import com.sanedge.pointofsale.models.order.OrderYearTotalRevenue;

@Repository
public interface OrderTotalRevenueByMerchantRepository extends JpaRepository<Order, Long> {
    @Query(value = """
            WITH monthly_revenue AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::INT AS year,
                    EXTRACT(MONTH FROM o.created_at)::INT AS month,
                    COALESCE(SUM(o.total_price), 0)::INT AS total_revenue
                FROM
                    orders o
                JOIN
                    order_items oi ON o.order_id = oi.order_id
                WHERE
                    o.deleted_at IS NULL
                    AND oi.deleted_at IS NULL
                    AND o.merchant_id = :merchantId
                    AND (
                        (EXTRACT(YEAR FROM o.created_at) = :year1 AND EXTRACT(MONTH FROM o.created_at) = :month1)
                        OR (EXTRACT(YEAR FROM o.created_at) = :year2 AND EXTRACT(MONTH FROM o.created_at) = :month2)
                    )
                GROUP BY
                    EXTRACT(YEAR FROM o.created_at),
                    EXTRACT(MONTH FROM o.created_at)
            ),
            all_months AS (
                SELECT :year1::TEXT AS year, :month1 AS month, TO_CHAR(TO_DATE(:month1::TEXT, 'MM'), 'FMMonth') AS month_name
                UNION
                SELECT :year2::TEXT AS year, :month2 AS month, TO_CHAR(TO_DATE(:month2::TEXT, 'MM'), 'FMMonth') AS month_name
            )
            SELECT
                am.year AS year,
                am.month_name AS month,
                COALESCE(mr.total_revenue, 0) AS totalRevenue
            FROM
                all_months am
            LEFT JOIN
                monthly_revenue mr ON am.year::INT = mr.year
                                 AND am.month = mr.month
            ORDER BY
                am.year DESC,
                am.month DESC
            """, nativeQuery = true)
    List<OrderMonthTotalRevenue> findMonthlyTotalRevenueByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year1") Integer year1,
            @Param("month1") Integer month1,
            @Param("year2") Integer year2,
            @Param("month2") Integer month2);

    @Query(value = """
            WITH yearly_revenue AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::INT AS year,
                    COALESCE(SUM(o.total_price), 0)::INT AS total_revenue
                FROM
                    orders o
                JOIN
                    order_items oi ON o.order_id = oi.order_id
                WHERE
                    o.deleted_at IS NULL
                    AND oi.deleted_at IS NULL
                    AND o.merchant_id = :merchantId
                    AND (
                        EXTRACT(YEAR FROM o.created_at) = :year
                        OR EXTRACT(YEAR FROM o.created_at) = :year - 1
                    )
                GROUP BY
                    EXTRACT(YEAR FROM o.created_at)
            ),
            all_years AS (
                SELECT :year AS year
                UNION
                SELECT :year - 1 AS year
            )
            SELECT
                ay.year::TEXT AS year,
                COALESCE(yr.total_revenue, 0) AS totalRevenue
            FROM
                all_years ay
            LEFT JOIN
                yearly_revenue yr ON ay.year = yr.year
            ORDER BY
                ay.year DESC
            """, nativeQuery = true)
    List<OrderYearTotalRevenue> findYearlyTotalRevenueByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year);
}
