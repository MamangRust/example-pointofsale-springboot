package com.sanedge.pointofsale;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.sanedge.pointofsale.repository.role.RoleCommandRepository;
import com.sanedge.pointofsale.repository.role.RoleQueryRepository;
import com.sanedge.pointofsale.repository.user.UserCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantCommandRepository;
import com.sanedge.pointofsale.repository.merchant.MerchantQueryRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.models.Role;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.models.Merchant;
import com.sanedge.pointofsale.models.UserRole;
import com.sanedge.pointofsale.repository.user_role.UserRoleRepository;
import com.sanedge.pointofsale.enums.Status;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired
    protected RoleCommandRepository roleCommandRepository;

    @Autowired
    protected RoleQueryRepository roleQueryRepository;

    @Autowired
    protected UserCommandRepository userCommandRepository;

    @Autowired
    protected UserQueryRepository userQueryRepository;

    @Autowired
    protected MerchantCommandRepository merchantCommandRepository;

    @Autowired
    protected MerchantQueryRepository merchantQueryRepository;

    @Autowired
    protected UserRoleRepository userRoleRepository;

    @Autowired
    protected jakarta.persistence.EntityManager entityManager;

    protected User adminUser;
    protected User regularUser;
    protected Merchant adminMerchant;

    @BeforeEach
    @Transactional
    void baseSetup() {
        entityManager.flush();

        ensureRoles();
        this.adminUser = ensureUsers();
        this.adminMerchant = ensureMerchant(this.adminUser);
        
        entityManager.flush();
        entityManager.clear();
    }

    protected void ensureRoles() {
        if (roleQueryRepository.findByRoleName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setRoleName("ROLE_ADMIN");
            roleCommandRepository.save(adminRole);
        }

        if (roleQueryRepository.findByRoleName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setRoleName("ROLE_USER");
            roleCommandRepository.save(userRole);
        }

        entityManager.flush();
    }

    protected User ensureUsers() {
        User admin = userQueryRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setFirstname("Admin");
            admin.setLastname("User");
            admin.setEmail("admin@example.com");
            admin.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2"); // "password"
            admin = userCommandRepository.save(admin);

            Role adminRole = roleQueryRepository.findByRoleName("ROLE_ADMIN").orElseThrow();
            UserRole adminUserRole = new UserRole();
            adminUserRole.setUser(admin);
            adminUserRole.setRole(adminRole);
            userRoleRepository.save(adminUserRole);
        }

        User user = userQueryRepository.findByUsername("user").orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername("user");
            user.setFirstname("Regular");
            user.setLastname("User");
            user.setEmail("user@example.com");
            user.setPassword("$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2"); // "password"
            user = userCommandRepository.save(user);

            Role userRole = roleQueryRepository.findByRoleName("ROLE_USER").orElseThrow();
            UserRole userUserRole = new UserRole();
            userUserRole.setUser(user);
            userUserRole.setRole(userRole);
            userRoleRepository.save(userUserRole);
        }

        this.regularUser = user;

        entityManager.flush();

        return admin;
    }

    protected Merchant ensureMerchant(User user) {
        return merchantQueryRepository.findByUserId(user.getUserId()).orElseGet(() -> {
            Merchant merchant = new Merchant();
            merchant.setUserId(user.getUserId());
            merchant.setName("Default Merchant");
            merchant.setDescription("Default Merchant Description");
            merchant.setAddress("Default Merchant Address");
            merchant.setContactEmail("merchant@example.com");
            merchant.setContactPhone("08123456789");
            merchant.setStatus(Status.SUCCESS);
            Merchant saved = merchantCommandRepository.save(merchant);
            entityManager.flush();
            return saved;
        });
    }
}