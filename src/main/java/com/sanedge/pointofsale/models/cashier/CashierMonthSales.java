package com.sanedge.pointofsale.models.cashier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashierMonthSales {
    private String month;
    private Integer cashierId;
    private String cashierName;
    private Integer orderCount;
    private Long totalSales;
}
