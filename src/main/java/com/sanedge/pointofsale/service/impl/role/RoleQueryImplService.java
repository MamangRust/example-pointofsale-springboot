package com.sanedge.pointofsale.service.impl.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.role.FindAllRoles;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.api.PaginationMeta;
import com.sanedge.pointofsale.domain.responses.role.RoleResponse;
import com.sanedge.pointofsale.domain.responses.role.RoleResponseDeleteAt;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.Role;
import com.sanedge.pointofsale.repository.role.RoleQueryRepository;
import com.sanedge.pointofsale.service.role.RoleQueryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleQueryImplService implements RoleQueryService {
        private RoleQueryRepository roleQueryRepository;

        @Autowired
        public RoleQueryImplService(RoleQueryRepository roleQueryRepository) {
                this.roleQueryRepository = roleQueryRepository;
        }

        @Override
        public ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching all roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<Role> rolePage = roleQueryRepository.findRoles(keyword, pageable);

                List<RoleResponse> responses = rolePage.getContent()
                                .stream()
                                .map(RoleResponse::from)
                                .toList();

                log.info("✅ Found {} roles", responses.size());

                return ApiResponsePagination.<List<RoleResponse>>builder()
                                .status("success")
                                .message("Roles retrieved successfully")
                                .data(responses)
                                .pagination(PaginationMeta.fromSpringPage(rolePage))
                                .build();
        }

        @Override
        public ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching active roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<Role> rolePage = roleQueryRepository.findActiveRoles(keyword, pageable);

                List<RoleResponseDeleteAt> responses = rolePage.getContent()
                                .stream()
                                .map(RoleResponseDeleteAt::from)
                                .toList();

                log.info("✅ Found {} active roles", responses.size());

                return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                .status("success")
                                .message("Active roles retrieved successfully")
                                .data(responses)
                                .pagination(PaginationMeta.fromSpringPage(rolePage))
                                .build();
        }

        @Override
        public ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching trashed roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<Role> rolePage = roleQueryRepository.findTrashedRoles(keyword, pageable);

                List<RoleResponseDeleteAt> responses = rolePage.getContent()
                                .stream()
                                .map(RoleResponseDeleteAt::from)
                                .toList();

                log.info("✅ Found {} trashed roles", responses.size());

                return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                .status("success")
                                .message("Trashed roles retrieved successfully")
                                .data(responses)
                                .pagination(PaginationMeta.fromSpringPage(rolePage))
                                .build();
        }

        @Override
        public ApiResponse<RoleResponse> findById(Integer id) {
                log.info("🔍 Finding role by id={}", id);

                Role role = roleQueryRepository.findById((long) id)
                                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

                return ApiResponse.<RoleResponse>builder()
                                .status("success")
                                .message("Role retrieved successfully")
                                .data(RoleResponse.from(role))
                                .build();
        }

        @Override
        public ApiResponse<List<RoleResponse>> findByUserId(Integer userId) {
                log.info("🔍 Finding roles by user_id={}", userId);

                List<Role> roles = roleQueryRepository.findUserRoles(userId.longValue());

                List<RoleResponse> responses = roles.stream()
                                .map(RoleResponse::from)
                                .toList();

                return ApiResponse.<List<RoleResponse>>builder()
                                .status("success")
                                .message("Roles retrieved successfully")
                                .data(responses)
                                .build();
        }

        @Override
        public ApiResponse<RoleResponse> findByName(String name) {
                log.info("🔍 Finding role by name={}", name);

                Role role = roleQueryRepository.findByRoleName(name)
                                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

                return ApiResponse.<RoleResponse>builder()
                                .status("success")
                                .message("Role retrieved successfully")
                                .data(RoleResponse.from(role))
                                .build();
        }
}
