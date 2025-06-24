package com.mdtalalwasim.ecommerce.repository;

import com.mdtalalwasim.ecommerce.entity.DeliveryAssignment;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryAssignmentRepository extends JpaRepository<DeliveryAssignment, Long> {

    Optional<DeliveryAssignment> findByOrder(ProductOrder order);
}
