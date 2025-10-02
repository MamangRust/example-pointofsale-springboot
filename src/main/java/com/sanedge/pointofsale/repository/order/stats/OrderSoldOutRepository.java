package com.sanedge.pointofsale.repository.order.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.order.Order;
import com.sanedge.pointofsale.models.order.OrderMonth;
import com.sanedge.pointofsale.models.order.OrderYear;

@Repository
public interface OrderSoldOutRepository extends JpaRepository<Order, Long> {

    @Query(value = """
            WITH date_range AS (
                SELECT
                    date_trunc('month', TO_TIMESTAMP(:yearMonth, 'YYYYMM')) AS start_date,
                    date_trunc('month', TO_TIMESTAMP(:yearMonth, 'YYYYMM')) + interval '1 year' - interval '1 day' AS end_date
            ),
            monthly_orders AS (
                SELECT
                    date_trunc('month', o.created_at) AS activity_month,
                    COUNT(o.order_id)::INT AS order_count,
                    SUM(o.total_price)::BIGINT AS total_revenue,
                    SUM(oi.quantity)::INT AS total_items_sold
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                WHERE o.deleted_at IS NULL
                  AND oi.deleted_at IS NULL
                  AND o.created_at BETWEEN (SELECT start_date FROM date_range)
                                       AND (SELECT end_date FROM date_range)
                GROUP BY activity_month
            )
            SELECT
                TO_CHAR(mo.activity_month, 'Mon') AS month,
                mo.order_count AS orderCount,
                mo.total_revenue AS totalRevenue,
                mo.total_items_sold AS totalItemsSold
            FROM monthly_orders mo
            ORDER BY mo.activity_month
            """, nativeQuery = true)
    List<OrderMonth> findMonthlyOrdersByYear(@Param("yearMonth") Integer yearMonth);

    @Query(value = """
            WITH last_five_years AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::TEXT AS year,
                    COUNT(o.order_id)::INT AS order_count,
                    SUM(o.total_price)::BIGINT AS total_revenue,
                    SUM(oi.quantity)::INT AS total_items_sold,
                    COUNT(DISTINCT o.user_id)::INT AS active_cashiers,
                    COUNT(DISTINCT oi.product_id)::INT AS unique_products_sold
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                WHERE o.deleted_at IS NULL
                  AND oi.deleted_at IS NULL
                  AND EXTRACT(YEAR FROM o.created_at) BETWEEN EXTRACT(YEAR FROM TO_TIMESTAMP(:yearMonth, 'YYYYMM')) - 4
                                                           AND EXTRACT(YEAR FROM TO_TIMESTAMP(:yearMonth, 'YYYYMM'))
                GROUP BY EXTRACT(YEAR FROM o.created_at)
            )
            SELECT
                year,
                order_count AS orderCount,
                total_revenue AS totalRevenue,
                total_items_sold AS totalItemsSold,
                active_cashiers AS activeCashiers,
                unique_products_sold AS uniqueProductsSold
            FROM last_five_years
            ORDER BY year
            """, nativeQuery = true)
    List<OrderYear> findYearlyOrders(@Param("yearMonth") Integer yearMonth);
}
