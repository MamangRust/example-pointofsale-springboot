package com.sanedge.pointofsale.repository.user_role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
