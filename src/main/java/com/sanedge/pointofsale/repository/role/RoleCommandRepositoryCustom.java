package com.sanedge.pointofsale.repository.role;

import com.sanedge.pointofsale.models.Role;

public interface RoleCommandRepositoryCustom {
    Role trashed(Long roleId);

    Role restore(Long roleId);

    Role deletePermanent(Long roleId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
