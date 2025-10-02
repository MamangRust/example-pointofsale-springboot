package com.sanedge.pointofsale.service.impl.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.user.FindAllUserRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.api.ApiResponsePagination;
import com.sanedge.pointofsale.domain.responses.api.PaginationMeta;
import com.sanedge.pointofsale.domain.responses.user.UserResponse;
import com.sanedge.pointofsale.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.service.user.UserQueryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserQueryImplService implements UserQueryService {
        private UserQueryRepository userQueryRepository;

        @Autowired
        public UserQueryImplService(UserQueryRepository userQueryRepository) {
                this.userQueryRepository = userQueryRepository;
        }

        @Override
        public ApiResponsePagination<List<UserResponse>> findAll(FindAllUserRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching all users | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<User> userPage = userQueryRepository.findAllUsers(keyword, pageable);

                List<UserResponse> userResponses = userPage.getContent()
                                .stream()
                                .map(UserResponse::from)
                                .toList();

                log.info("✅ Found {} users", userResponses.size());

                return ApiResponsePagination.<List<UserResponse>>builder()
                                .status("success")
                                .message("Users retrieved successfully")
                                .data(userResponses)
                                .pagination(PaginationMeta.fromSpringPage(userPage))
                                .build();
        }

        @Override
        public ApiResponse<UserResponse> findById(Integer userId) {
                log.info("🔍 Fetching user by id={}", userId);

                User user = userQueryRepository.findByUserId(userId)
                                .orElseThrow(() -> {
                                        log.error("❌ User not found | id={}", userId);
                                        return new ResourceNotFoundException("User not found");
                                });

                return ApiResponse.<UserResponse>builder()
                                .status("success")
                                .message("User retrieved successfully")
                                .data(UserResponse.from(user))
                                .build();
        }

        @Override
        public ApiResponsePagination<List<UserResponseDeleteAt>> findByActive(FindAllUserRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Fetching active users | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<User> userPage = userQueryRepository.findActiveUsers(keyword, pageable);

                List<UserResponseDeleteAt> userResponses = userPage.getContent()
                                .stream()
                                .map(UserResponseDeleteAt::from)
                                .toList();

                log.info("✅ Found {} active users", userResponses.size());

                return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                                .status("success")
                                .message("Active users retrieved successfully")
                                .data(userResponses)
                                .pagination(PaginationMeta.fromSpringPage(userPage))
                                .build();
        }

        @Override
        public ApiResponsePagination<List<UserResponseDeleteAt>> findByTrashed(FindAllUserRequest req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🗑️ Fetching trashed users | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                Pageable pageable = PageRequest.of(page, pageSize);
                Page<User> userPage = userQueryRepository.findTrashedUsers(keyword, pageable);

                List<UserResponseDeleteAt> userResponses = userPage.getContent()
                                .stream()
                                .map(UserResponseDeleteAt::from)
                                .toList();

                log.info("🗑️ Found {} trashed users", userResponses.size());

                return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                                .status("success")
                                .message("Trashed users retrieved successfully")
                                .data(userResponses)
                                .pagination(PaginationMeta.fromSpringPage(userPage))
                                .build();
        }

}
