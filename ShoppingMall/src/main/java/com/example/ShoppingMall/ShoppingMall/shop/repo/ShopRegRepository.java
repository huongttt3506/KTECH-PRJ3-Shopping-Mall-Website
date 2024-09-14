package com.example.ShoppingMall.ShoppingMall.shop.repo;

import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopRegStatus;
import com.example.ShoppingMall.ShoppingMall.shop.entity.ShopRegistration;
import com.example.ShoppingMall.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRegRepository extends JpaRepository<ShopRegistration, Long> {

    Optional<ShopRegistration> findByOwnerAndBusinessNum(UserEntity owner, String businessNum);
    @Query("SELECT sr FROM ShopRegistration sr WHERE sr.owner.id = :ownerId AND sr.businessNum = :businessNum AND sr.status = :status")
    boolean existsByOwnerIdAndBusinessNumAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("businessNum") String businessNum,
            @Param("status") ShopRegStatus status
    );

    @Query("SELECT sr FROM ShopRegistration sr WHERE sr.owner.id = :ownerId")
    List<ShopRegistration> findAllByOwnerId(@Param("ownerId") Long ownerId);

    boolean existsByOwnerIdAndStatus(Long ownerId, ShopRegStatus status);

    boolean existsByShopIdAndStatus(Long shopId, ShopRegStatus shopRegStatus);
}
