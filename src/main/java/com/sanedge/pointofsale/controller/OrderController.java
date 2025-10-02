package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.order.CreateOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.FindAllOrderByMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.FindAllOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.MonthOrderMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.MonthTotalRevenue;
import com.sanedge.pointofsale.domain.requests.order.MonthTotalRevenueMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.UpdateOrderRequest;
import com.sanedge.pointofsale.domain.requests.order.YearOrderMerchantRequest;
import com.sanedge.pointofsale.domain.requests.order.YearTotalRevenueMerchantRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderMonthlyTotalRevenueResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderResponseDeleteAt;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyResponse;
import com.sanedge.pointofsale.domain.responses.order.OrderYearlyTotalRevenueResponse;
import com.sanedge.pointofsale.service.order.OrderCommandService;
import com.sanedge.pointofsale.service.order.OrderQueryService;
import com.sanedge.pointofsale.service.order.stats.OrderSoldoutService;
import com.sanedge.pointofsale.service.order.stats.OrderTotalRevenueService;
import com.sanedge.pointofsale.service.order.statsbymerchant.OrderSoldOutByMerchantService;
import com.sanedge.pointofsale.service.order.statsbymerchant.OrderTotalRevenueByMerchantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final OrderCommandService orderCommandService;
    private final OrderSoldoutService orderSoldoutService;
    private final OrderTotalRevenueService orderTotalRevenueService;
    private final OrderSoldOutByMerchantService orderSoldOutByMerchantService;
    private final OrderTotalRevenueByMerchantService orderTotalRevenueByMerchantService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<OrderResponse>>> findAll(
            @ModelAttribute FindAllOrderRequest req) {
        return ResponseEntity.ok(orderQueryService.findAll(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderQueryService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<OrderResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllOrderRequest req) {
        return ResponseEntity.ok(orderQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<OrderResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllOrderRequest req) {

        return ResponseEntity.ok(orderQueryService.findByTrashed(req));
    }

    @GetMapping("/merchant/{merchant_id}")
    public ResponseEntity<ApiResponsePagination<List<OrderResponse>>> findByMerchant(
            @PathVariable Integer merchant_id,
            @ModelAttribute FindAllOrderByMerchantRequest req) {
        req.setMerchantId(merchant_id);

        return ResponseEntity.ok(orderQueryService.findByMerchantId(req));
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<ApiResponse<List<OrderMonthlyResponse>>> findMonthlyRevenue(
            @RequestParam Integer yearMonth) {
        return ResponseEntity.ok(orderSoldoutService.findMonthlyOrders(yearMonth));
    }

    @GetMapping("/yearly-revenue")
    public ResponseEntity<ApiResponse<List<OrderYearlyResponse>>> findYearlyRevenue(
            @RequestParam Integer year) {
        return ResponseEntity.ok(orderSoldoutService.findYearlyOrders(year));
    }

    @GetMapping("/monthly-total-revenue")
    public ResponseEntity<ApiResponse<List<OrderMonthlyTotalRevenueResponse>>> findMonthlyTotalRevenue(
            MonthTotalRevenue req) {
        return ResponseEntity.ok(orderTotalRevenueService.findMonthlyStats(req));
    }

    @GetMapping("/yearly-total-revenue")
    public ResponseEntity<ApiResponse<List<OrderYearlyTotalRevenueResponse>>> findYearlyTotalRevenue(
            @RequestParam Integer year) {
        return ResponseEntity.ok(orderTotalRevenueService.findYearlyStats(year));
    }

    @GetMapping("/merchant/monthly-revenue")
    public ResponseEntity<ApiResponse<List<OrderMonthlyResponse>>> findMonthlyRevenueByMerchant(
            MonthOrderMerchantRequest req) {
        return ResponseEntity.ok(orderSoldOutByMerchantService.findMonthlyOrdersByMerchant(req));
    }

    @GetMapping("/merchant/yearly-revenue")
    public ResponseEntity<ApiResponse<List<OrderYearlyResponse>>> findYearlyRevenueByMerchant(
            YearOrderMerchantRequest req) {
        return ResponseEntity.ok(orderSoldOutByMerchantService.findYearlyOrdersByMerchant(req));
    }

    @GetMapping("/merchant/monthly-total-revenue")
    public ResponseEntity<ApiResponse<List<OrderMonthlyTotalRevenueResponse>>> findMonthlyTotalRevenueByMerchant(
            MonthTotalRevenueMerchantRequest req) {
        return ResponseEntity.ok(orderTotalRevenueByMerchantService.findMonthlyStatsByMerchant(req));
    }

    @GetMapping("/merchant/yearly-total-revenue")
    public ResponseEntity<ApiResponse<List<OrderYearlyTotalRevenueResponse>>> findYearlyTotalRevenueByMerchant(
            YearTotalRevenueMerchantRequest req) {
        return ResponseEntity.ok(orderTotalRevenueByMerchantService.findYearlyStatsByMerchant(req));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderCommandService.create(req));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateOrderRequest req) {
        req.setOrderId(id);
        return ResponseEntity.ok(orderCommandService.update(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDeleteAt>> trashedOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderCommandService.trash(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDeleteAt>> restoreOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderCommandService.restore(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteOrderPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(orderCommandService.delete(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllOrders() {
        return ResponseEntity.ok(orderCommandService.restoreAll());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllOrdersPermanent() {
        return ResponseEntity.ok(orderCommandService.deleteAll());
    }
}
