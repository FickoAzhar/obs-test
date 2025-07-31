package com.example.obs_test.repository;

import com.example.obs_test.entity.Inventory;
import com.example.obs_test.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByItem(Item item);
}
