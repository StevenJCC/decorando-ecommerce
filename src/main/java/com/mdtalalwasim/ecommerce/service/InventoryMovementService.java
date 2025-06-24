package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.dto.MovementCountDto;
import com.mdtalalwasim.ecommerce.entity.InventoryMovement;
import com.mdtalalwasim.ecommerce.entity.MovementType;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryMovementService {

    InventoryMovement saveMovement(InventoryMovement movement);
    List<InventoryMovement> getAllMovements();
    List<InventoryMovement> findMovements(MovementType type, LocalDateTime from, LocalDateTime to);

    /**
     * Agrupa y cuenta movimientos por tipo.
     * @return lista DTO con tipo y cantidad
     */
    List<MovementCountDto> getMovementCounts();
}
