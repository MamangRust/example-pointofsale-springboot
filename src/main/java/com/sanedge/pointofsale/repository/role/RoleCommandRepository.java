package com.sanedge.pointofsale.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.Role;

@Repository
public interface RoleCommandRepository extends JpaRepository<Role, Long>, RoleCommandRepositoryCustom {
}
