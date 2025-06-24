// src/main/java/com/mdtalalwasim/ecommerce/repository/InventoryMovementRepository.java
package com.mdtalalwasim.ecommerce.repository;

import com.mdtalalwasim.ecommerce.dto.MovementCountDto;
import com.mdtalalwasim.ecommerce.entity.InventoryMovement;
import com.mdtalalwasim.ecommerce.entity.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    // --- Métodos de filtrado existentes ---
    List<InventoryMovement> findByMovementType(MovementType movementType);
    List<InventoryMovement> findByMovementDateBetween(LocalDateTime start, LocalDateTime end);
    List<InventoryMovement> findByMovementTypeAndMovementDateBetween(
        MovementType movementType, LocalDateTime start, LocalDateTime end);

    // --- Nuevo método para conteo por tipo ---
    @Query("SELECT new com.mdtalalwasim.ecommerce.dto.MovementCountDto(" +
           "m.movementType, COUNT(m)) " +
           "FROM InventoryMovement m " +
           "GROUP BY m.movementType")
    List<MovementCountDto> findCountsByType();
    @Query("SELECT new com.mdtalalwasim.ecommerce.dto.MovementCountDto(m.movementType, COUNT(m)) " +
       "FROM InventoryMovement m GROUP BY m.movementType")
List<MovementCountDto> countMovementsByType();

}
