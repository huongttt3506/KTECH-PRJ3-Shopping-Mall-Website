package com.example.ShoppingMall.user.repo;

import com.example.ShoppingMall.user.entity.BusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<BusinessRegistration, Long> {
    boolean existsByBusinessNum(String businessNum);
}
