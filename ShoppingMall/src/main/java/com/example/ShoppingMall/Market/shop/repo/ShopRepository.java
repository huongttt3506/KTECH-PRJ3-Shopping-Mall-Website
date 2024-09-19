package com.example.ShoppingMall.Market.shop.repo;

import com.example.ShoppingMall.Market.shop.entity.ShopCategory;
import com.example.ShoppingMall.Market.shop.entity.ShopEntity;
import com.example.ShoppingMall.Market.shop.entity.ShopStatus;
import com.example.ShoppingMall.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    Optional<ShopEntity> findByOwnerId(Long id);

    List<ShopEntity> findByOwner(UserEntity owner);

    List<ShopEntity> findAllByStatus(ShopStatus status);

    List<ShopEntity> findAllByCategory(ShopCategory category);

    @Query("SELECT s FROM ShopEntity s ORDER BY s.lastPurchased DESC")
    List<ShopEntity> findAllByOrderByLastPurchasedDesc();

    @Query("SELECT s FROM ShopEntity s WHERE s.name LIKE %:nameKeyword%")
    List<ShopEntity> findAllByNameContaining(@Param("nameKeyword") String nameKeyword);


    // Find all shops by name containing nameKeyword and by category
    @Query("SELECT s FROM ShopEntity s WHERE s.name LIKE %:nameKeyword% AND s.category = :category")
    List<ShopEntity> findAllByNameContainingAndCategory(
            @Param("nameKeyword") String nameKeyword,
            @Param("category") ShopCategory category
    );

    boolean existsByBusinessNum(String businessNum);

    ShopEntity findByBusinessNum(String businessNum);
}