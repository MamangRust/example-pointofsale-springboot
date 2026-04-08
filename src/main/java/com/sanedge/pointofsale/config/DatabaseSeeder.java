package com.sanedge.pointofsale.config; // Sesuaikan dengan package Anda

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.Product;
import com.sanedge.pointofsale.models.Role;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.models.cashier.Cashier;
import com.sanedge.pointofsale.models.category.Category;
import com.sanedge.pointofsale.repository.cashier.CashierCommandRepository;
import com.sanedge.pointofsale.repository.cashier.CashierQueryRepository;
import com.sanedge.pointofsale.repository.category.CategoryCommandRepository;
import com.sanedge.pointofsale.repository.category.CategoryQueryRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantQueryRepository;
import com.sanedge.pointofsale.repository.product.ProductCommandRepository;
import com.sanedge.pointofsale.repository.product.ProductQueryRepository;
import com.sanedge.pointofsale.repository.role.RoleCommandRepository;
import com.sanedge.pointofsale.repository.role.RoleQueryRepository;
import com.sanedge.pointofsale.repository.user.UserCommandRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserQueryRepository userQueryRepository;
    private final RoleQueryRepository roleQueryRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final ProductQueryRepository productQueryRepository;
    private final CashierQueryRepository cashierQueryRepository; // Ditambahkan

    private final UserCommandRepository userCommandRepository;
    private final RoleCommandRepository roleCommandRepository;
    private final MerchantCommandRepository merchantCommandRepository;
    private final CategoryCommandRepository categoryCommandRepository;
    private final ProductCommandRepository productCommandRepository;
    private final CashierCommandRepository cashierCommandRepository; 

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            seedRoles();
            seedUsers();
            seedCategories();
            seedMerchants();
            seedProducts();
            seedCashiers();
            
            log.info("Database seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during database seeding: {}", e.getMessage());
        }
    }

    private void seedRoles() {
        if (roleQueryRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");

            Role userRole = new Role();
            userRole.setRoleName("USER");

            roleCommandRepository.saveAll(Arrays.asList(adminRole, userRole));
            log.info("Roles seeded.");
        }
    }

    private void seedUsers() {
        if (userQueryRepository.count() == 0) {
            Role adminRole = roleQueryRepository.findByRoleName("ADMIN").orElse(null);
            Role userRole = roleQueryRepository.findByRoleName("USER").orElse(null);

            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstname("Admin");
            admin.setLastname("User");
            admin.setEmail("admin@sanedge.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));

            User user = new User();
            user.setUsername("user");
            user.setFirstname("John");
            user.setLastname("Doe");
            user.setEmail("user@sanedge.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

            userCommandRepository.saveAll(Arrays.asList(admin, user));
            log.info("Users seeded.");
        }
    }

    private void seedCategories() {
        if (categoryQueryRepository.count() == 0) {
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Electronic devices and gadgets");
            electronics.setSlugCategory("electronics");

            Category fashion = new Category();
            fashion.setName("Fashion");
            fashion.setDescription("Clothing and accessories");
            fashion.setSlugCategory("fashion");

            categoryCommandRepository.saveAll(Arrays.asList(electronics, fashion));
            log.info("Categories seeded.");
        }
    }

    private void seedMerchants() {
        if (merchantQueryRepository.count() == 0) {
            User admin = userQueryRepository.findByEmail("admin@sanedge.com").orElse(null);
            if (admin != null) {
                Merchant merchant = new Merchant();
                merchant.setUserId(admin.getUserId().longValue());
                merchant.setName("Sanedge Official Store");
                merchant.setDescription("Official store for Sanedge products");
                merchant.setAddress("Jakarta, Indonesia");
                merchant.setContactEmail("contact@sanedge.com");
                merchant.setContactPhone("08123456789");

                merchantCommandRepository.save(merchant);
                log.info("Merchants seeded.");
            }
        }
    }

    private void seedProducts() {
        if (productQueryRepository.count() == 0) {
            Merchant merchant = merchantQueryRepository.findAll().stream().findFirst().orElse(null);
            Category category = categoryQueryRepository.findAll().stream().findFirst().orElse(null);

            if (merchant != null && category != null) {
                Product product = new Product();
                product.setMerchantId(merchant.getMerchantId().longValue());
                product.setCategoryId(category.getCategoryId().longValue());
                product.setName("Smartphone X");
                product.setDescription("Latest smartphone with advanced features");
                product.setPrice(12000000);
                product.setCountInStock(50);
                product.setBrand("BrandX");
                product.setWeight(200);
                product.setSlugProduct("smartphone-x");
                product.setImageProduct("http://example.com/smartphone_x.jpg");

                productCommandRepository.save(product);
                log.info("Products seeded.");
            }
        }
    }

    private void seedCashiers() {
        if (cashierQueryRepository.count() == 0) {
            User admin = userQueryRepository.findByEmail("admin@sanedge.com").orElse(null);
            Merchant merchant = merchantQueryRepository.findAll().stream().findFirst().orElse(null);

            if (admin != null && merchant != null) {
                Cashier cashier = new Cashier();
                cashier.setUserId(admin.getUserId());
                cashier.setMerchantId(merchant.getMerchantId());
                cashier.setName("Kasir Utama");

                cashierCommandRepository.save(cashier);
                log.info("Cashiers seeded.");
            }
        }
    }
}