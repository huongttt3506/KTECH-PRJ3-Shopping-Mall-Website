package com.example.ShoppingMall.Market.item.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = false)
    //"fashion", "baby", "men fashion", woman fashion", "toys"...
    private String name;

    @ManyToMany(mappedBy = "category")
    private Set<ItemEntity> items = new HashSet<>();
}
