package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.user.CreateUserRequest;
import com.sanedge.pointofsale.domain.requests.user.FindAllUserRequest;
import com.sanedge.pointofsale.domain.requests.user.UpdateUserRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.pointofsale.service.user.UserCommandService;
import com.sanedge.pointofsale.service.user.UserQueryService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Autowired
    public UserController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<UserResponse>>> findAll(
            @ModelAttribute FindAllUserRequest req) {
        return ResponseEntity.ok(userQueryService.findAll(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(userQueryService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<UserResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllUserRequest req) {
        return ResponseEntity.ok(userQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<UserResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllUserRequest req) {
        return ResponseEntity.ok(userQueryService.findByTrashed(req));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody CreateUserRequest req) {
        return ResponseEntity.ok(userCommandService.create(req));
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<UserResponse>> update(@RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(userCommandService.update(req));
    }

    @PostMapping("/trash/{id}")
    public ResponseEntity<ApiResponse<UserResponseDeleteAt>> trash(@PathVariable Integer id) {
        return ResponseEntity.ok(userCommandService.trashed(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<UserResponseDeleteAt>> restore(@PathVariable Integer id) {
        return ResponseEntity.ok(userCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(userCommandService.deletePermanent(id));
    }

    @PostMapping("/restore-all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(userCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(userCommandService.deleteAll());
    }
}