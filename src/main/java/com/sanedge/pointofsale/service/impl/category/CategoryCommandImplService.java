package com.sanedge.pointofsale.service.impl.category;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.domain.requests.category.CreateCategoryRequest;
import com.sanedge.pointofsale.domain.requests.category.UpdateCategoryRequest;
import com.sanedge.pointofsale.domain.responses.api.ApiResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponse;
import com.sanedge.pointofsale.domain.responses.category.CategoryResponseDeleteAt;
import com.sanedge.pointofsale.exception.ResourceNotFoundException;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.repository.category.CategoryCommandRepository;
import com.sanedge.pointofsale.repository.category.CategoryQueryRepository;
import com.sanedge.pointofsale.service.category.CategoryCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryCommandImplService implements CategoryCommandService {
    private final CategoryQueryRepository categoryQueryRepository;
    private final CategoryCommandRepository categoryCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<CategoryResponse> createCategory(CreateCategoryRequest req) {
        try {
            validateRequest(req);

            log.info("🆕 Creating category name={}", req.getName());

            categoryQueryRepository.findByName(req.getName())
                    .ifPresent(c -> {
                        log.warn("❌ Category creation failed. Category name '{}' already exists", req.getName());
                        throw new IllegalArgumentException("Category with name '" + req.getName() + "' already exists");
                    });

            Category category = new Category();
            category.setName(req.getName());
            category.setDescription(req.getDescription());
            category.setSlugCategory(req.getSlugCategory());
            category.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Category savedCategory = categoryCommandRepository.save(category);
            CategoryResponse response = CategoryResponse.from(savedCategory);

            log.info("✅ Category created successfully with id={}", response.getId());

            return ApiResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("✅ Category created successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create category", e);
            return ApiResponse.<CategoryResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponse> updateCategory(UpdateCategoryRequest req) {
        try {
            validateRequest(req);

            if (req.getCategoryId() == null) {
                throw new ResourceNotFoundException("category_id is required");
            }

            log.info("🔄 Updating category id={}", req.getCategoryId());

            Category category = categoryCommandRepository.findById(req.getCategoryId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            category.setName(req.getName());
            category.setDescription(req.getDescription());
            category.setSlugCategory(req.getSlugCategory());
            category.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Category updatedCategory = categoryCommandRepository.save(category);
            CategoryResponse response = CategoryResponse.from(updatedCategory);

            log.info("✅ Category updated successfully with id={}", response.getId());

            return ApiResponse.<CategoryResponse>builder()
                    .status("success")
                    .message("✅ Category updated successfully!")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update category id={}", req.getCategoryId(), e);
            return ApiResponse.<CategoryResponse>builder()
                    .status("error")
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponseDeleteAt> trashedCategory(Integer categoryId) {
        log.info("🗑️ Trashing category id={}", categoryId);
        try {
            Category category = categoryCommandRepository.trashed(categoryId.longValue());
            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Category trashed successfully!")
                    .data(CategoryResponseDeleteAt.from(category))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash category id={}", categoryId, e);
            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash category: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CategoryResponseDeleteAt> restoreCategory(Integer categoryId) {
        log.info("♻️ Restoring category id={}", categoryId);
        try {
            Category category = categoryCommandRepository.restore(categoryId.longValue());
            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Category restored successfully!")
                    .data(CategoryResponseDeleteAt.from(category))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore category id={}", categoryId, e);
            return ApiResponse.<CategoryResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore category: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteCategoryPermanent(Integer categoryId) {
        log.info("🧨 Permanently deleting category id={}", categoryId);
        try {
            categoryCommandRepository.deletePermanent(categoryId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Category permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete category id={}", categoryId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete category: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAllCategories() {
        log.info("🔄 Restoring ALL trashed categories");
        try {
            categoryCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All categories restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all categories", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all categories: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllCategoriesPermanent() {
        log.info("💣 Permanently deleting ALL trashed categories");
        try {
            categoryCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All categories permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all categories", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all categories: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb);
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
