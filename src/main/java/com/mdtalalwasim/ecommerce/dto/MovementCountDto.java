// src/main/java/com/mdtalalwasim/ecommerce/dto/MovementCountDto.java
package com.mdtalalwasim.ecommerce.dto;

import com.mdtalalwasim.ecommerce.entity.MovementType;

public class MovementCountDto {
    private MovementType type;
    private Long count;

    public MovementCountDto(MovementType type, Long count) {
        this.type = type;
        this.count = count;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

