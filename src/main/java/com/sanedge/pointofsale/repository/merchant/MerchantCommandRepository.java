package com.sanedge.pointofsale.repository.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.pointofsale.models.Merchant;

@Repository
public interface MerchantCommandRepository extends JpaRepository<Merchant, Long>, MerchantCommandRepositoryCustom {
}