package com.example.ShoppingMall.Market.item.repo;

import com.example.ShoppingMall.Market.item.entity.ItemEntity;
import com.example.ShoppingMall.Market.item.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {


    List<ItemEntity> findAllByShopId(Long shopId);

    @Query("SELECT i FROM ItemEntity i " +
            "WHERE (:shopId IS NULL OR i.shop.id = :shopId) " +
            "AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minPrice IS NULL OR i.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR i.price <= :maxPrice)")
    List<ItemEntity> searchItems(@Param("shopId") Long shopId,
                                 @Param("name") String name,
                                 @Param("minPrice") Integer minPrice,
                                 @Param("maxPrice") Integer maxPrice);


}
