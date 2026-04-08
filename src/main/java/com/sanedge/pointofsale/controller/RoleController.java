package com.sanedge.pointofsale.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.pointofsale.domain.requests.role.CreateRoleRequest;
import com.sanedge.pointofsale.domain.requests.role.FindAllRoles;
import com.sanedge.pointofsale.domain.requests.role.UpdateRoleRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.role.RoleResponse;
import com.sanedge.pointofsale.domain.responses.role.RoleResponseDeleteAt;
import com.sanedge.pointofsale.service.role.RoleCommandService;
import com.sanedge.pointofsale.service.role.RoleQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleQueryService roleQueryService;
    private final RoleCommandService roleCommandService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<RoleResponse>>> findAllRoles(
            @ModelAttribute FindAllRoles req) {
        return ResponseEntity.ok(roleQueryService.findAll(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<RoleResponseDeleteAt>>> findActiveRoles(
            @ModelAttribute FindAllRoles req) {
        return ResponseEntity.ok(roleQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<RoleResponseDeleteAt>>> findTrashedRoles(
            @ModelAttribute FindAllRoles req) {
        return ResponseEntity.ok(roleQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<RoleResponse>> findRoleById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleQueryService.findById(id));
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findRolesByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(roleQueryService.findByUserId(userId));
    }

    @GetMapping("/by-name")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<RoleResponse>> findRoleByName(@RequestParam String name) {
        return ResponseEntity.ok(roleQueryService.findByName(name));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(roleCommandService.create(request));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(roleCommandService.update(request));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponseDeleteAt>> trashRole(@PathVariable Integer id) {
        return ResponseEntity.ok(roleCommandService.trash(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoleResponseDeleteAt>> restoreRole(@PathVariable Integer id) {
        return ResponseEntity.ok(roleCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteRole(@PathVariable Integer id) {
        return ResponseEntity.ok(roleCommandService.delete(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllRoles() {
        return ResponseEntity.ok(roleCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllRoles() {
        return ResponseEntity.ok(roleCommandService.deleteAll());
    }
}
