package com.sanedge.pointofsale.domain.responses.order;

import java.util.List;

import com.sanedge.pointofsale.models.order.OrderMonthTotalRevenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMonthlyTotalRevenueResponse {
    private String year;
    private String month;
    private Long totalRevenue;

    public static OrderMonthlyTotalRevenueResponse from(OrderMonthTotalRevenue response) {
        return OrderMonthlyTotalRevenueResponse.builder()
                .year(response.getYear())
                .month(response.getMonth())
                .totalRevenue((long) response.getTotalRevenue())
                .build();
    }

    public static List<OrderMonthlyTotalRevenueResponse> fromList(List<OrderMonthTotalRevenue> responses) {
        if (responses == null)
            return List.of();
        return responses.stream().map(OrderMonthlyTotalRevenueResponse::from).toList();
    }
}