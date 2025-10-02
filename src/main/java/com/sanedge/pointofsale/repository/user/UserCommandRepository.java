package com.sanedge.pointofsale.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserCommandRepository extends JpaRepository<User, Long> {
  @Modifying
  @Transactional
  @Query(value = """
      UPDATE users
      SET deleted_at = CURRENT_TIMESTAMP
      WHERE id = :userId
        AND deleted_at IS NULL
      RETURNING *
      """, nativeQuery = true)
  User trashed(@Param("userId") Long userId);

  @Modifying
  @Transactional
  @Query(value = """
      UPDATE users
      SET deleted_at = NULL
      WHERE id = :userId
        AND deleted_at IS NOT NULL
      RETURNING *
      """, nativeQuery = true)
  User restore(@Param("userId") Long userId);

  @Modifying
  @Transactional
  @Query("DELETE FROM User u WHERE u.id = :userId AND u.deletedAt IS NOT NULL")
  int deletePermanent(@Param("userId") Long userId);

  @Modifying
  @Transactional
  @Query("UPDATE User u SET u.deletedAt = NULL WHERE u.deletedAt IS NOT NULL")
  int restoreAllDeleted();

  @Modifying
  @Transactional
  @Query("DELETE FROM User u WHERE u.deletedAt IS NOT NULL")
  int deleteAllDeleted();
}
