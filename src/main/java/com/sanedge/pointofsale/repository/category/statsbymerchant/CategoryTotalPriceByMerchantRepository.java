package com.sanedge.pointofsale.repository.category.statsbymerchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.models.category.CategoryMonthTotalPrice;
import com.sanedge.pointofsale.models.category.CategoryYearTotalPrice;

@Repository
public interface CategoryTotalPriceByMerchantRepository extends JpaRepository<Category, Long> {
    @Query(value = """
            WITH date_range AS (
                SELECT
                    make_date(:startYear, :startMonth, 1) AS start_date,
                    (make_date(:endYear, :endMonth, 1) + interval '1 month' - interval '1 day') AS end_date
            ),
            monthly_totals AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::TEXT AS year,
                    EXTRACT(MONTH FROM o.created_at)::INTEGER AS month,
                    COALESCE(SUM(o.total_price), 0)::BIGINT AS totalRevenue
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                JOIN products p ON oi.product_id = p.product_id
                JOIN categories c ON p.category_id = c.category_id
                WHERE o.deleted_at IS NULL
                  AND oi.deleted_at IS NULL
                  AND p.deleted_at IS NULL
                  AND c.deleted_at IS NULL
                  AND o.merchant_id = :merchantId
                  AND o.created_at BETWEEN (SELECT start_date FROM date_range) AND (SELECT end_date FROM date_range)
                GROUP BY EXTRACT(YEAR FROM o.created_at), EXTRACT(MONTH FROM o.created_at)
            ),
            all_months AS (
                SELECT :startYear::TEXT AS year, :startMonth AS month, TO_CHAR(make_date(:startYear, :startMonth, 1), 'FMMonth') AS month_name
                UNION
                SELECT :endYear::TEXT AS year, :endMonth AS month, TO_CHAR(make_date(:endYear, :endMonth, 1), 'FMMonth') AS month_name
            )
            SELECT
                am.year,
                am.month_name AS month,
                COALESCE(mt.totalRevenue, 0)::BIGINT AS totalRevenue
            FROM all_months am
            LEFT JOIN monthly_totals mt ON am.year = mt.year AND am.month = mt.month
            ORDER BY am.year::INT DESC, am.month DESC
            """, nativeQuery = true)
    List<CategoryMonthTotalPrice> findMonthlyTotalPriceByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth);

    @Query(value = """
            WITH yearly_data AS (
                SELECT
                    EXTRACT(YEAR FROM o.created_at)::INTEGER AS year,
                    COALESCE(SUM(o.total_price), 0)::BIGINT AS totalRevenue
                FROM orders o
                JOIN order_items oi ON o.order_id = oi.order_id
                JOIN products p ON oi.product_id = p.product_id
                JOIN categories c ON p.category_id = c.category_id
                WHERE o.deleted_at IS NULL
                  AND oi.deleted_at IS NULL
                  AND p.deleted_at IS NULL
                  AND c.deleted_at IS NULL
                  AND o.merchant_id = :merchantId
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
                COALESCE(yd.totalRevenue, 0)::BIGINT AS totalRevenue
            FROM all_years a
            LEFT JOIN yearly_data yd ON a.year = yd.year
            ORDER BY a.year DESC
            """, nativeQuery = true)
    List<CategoryYearTotalPrice> findYearlyTotalPriceByMerchant(
            @Param("merchantId") Long merchantId,
            @Param("year") Integer year,
            @Param("yearMinusOne") Integer yearMinusOne);
}
