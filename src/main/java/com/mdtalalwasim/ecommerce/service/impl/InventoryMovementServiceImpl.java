package com.mdtalalwasim.ecommerce.service.impl;

import com.mdtalalwasim.ecommerce.dto.MovementCountDto;
import com.mdtalalwasim.ecommerce.entity.InventoryMovement;
import com.mdtalalwasim.ecommerce.entity.MovementType;
import com.mdtalalwasim.ecommerce.repository.InventoryMovementRepository;
import com.mdtalalwasim.ecommerce.service.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository repo;

    @Override
    public InventoryMovement saveMovement(InventoryMovement movement) {
        return repo.save(movement);
    }

    @Override
    public List<InventoryMovement> getAllMovements() {
        return repo.findAll();
    }

    @Override
    public List<InventoryMovement> findMovements(MovementType type, LocalDateTime from, LocalDateTime to) {
        if (type != null && from != null && to != null) {
            return repo.findByMovementTypeAndMovementDateBetween(type, from, to);
        }
        if (type != null) {
            return repo.findByMovementType(type);
        }
        if (from != null && to != null) {
            return repo.findByMovementDateBetween(from, to);
        }
        return repo.findAll();
    }

    @Override
    public List<MovementCountDto> getMovementCounts() {
        return repo.findCountsByType();
    }
}
