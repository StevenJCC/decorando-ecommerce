package com.mdtalalwasim.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mdtalalwasim.ecommerce.entity.OrderAddress;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {
}
